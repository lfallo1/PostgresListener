package com.mygene2.postgreslistener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import com.mygene2.postgreslistener.mapper.AccountRowMapper;
import com.mygene2.postgreslistener.model.Account;
import com.mygene2.postgreslistener.model.Config;
import com.mygene2.postgreslistener.model.EmailConfig;
import com.mygene2.postgreslistener.service.EmailConfigFactory;
import com.mygene2.postgreslistener.service.EmailService;
import com.mygene2.postgreslistener.service.MyGeneVelocityEngine;

public class Driver {

	private static final String DATABASE_DRIVER_NAME = "org.postgresql.Driver";
	private static final String POSTGRES_NOTIFIER = "postgres.notifier";
	private static final String JDBC_PASSWORD = "jdbc.password";
	private static final String JDBC_USERNAME = "jdbc.username";
	private static final String JDBC_URL = "jdbc.url";
	public static final String DRIVER_PROPERTIES = "driver.properties";

	public static void main(String args[]) throws Exception {
		
		//load properties
		Properties props = new Properties();
		InputStream in = Driver.class.getClassLoader().getResourceAsStream(DRIVER_PROPERTIES);
		props.load(in);
		String url = props.getProperty(JDBC_URL);
		String username = props.getProperty(JDBC_USERNAME);
		String password = props.getProperty(JDBC_PASSWORD);
		String notifierKey = props.getProperty(POSTGRES_NOTIFIER);
		in.close();
		
		// Create connection, one for the notifier for the listener
		Class.forName(DATABASE_DRIVER_NAME);
		Connection lConn = DriverManager.getConnection(url, username, password);
		
		//load dependencies
		AccountRowMapper mapper = new AccountRowMapper();
		Config config = new Config();
		VelocityEngine velocityEngine = new MyGeneVelocityEngine();
		EmailService emailService = new EmailService(config, velocityEngine);
		EmailConfigFactory emailConfigFactory = new EmailConfigFactory(config);

		// Create threads on which notifications are received
		MyGeneInactiveAccountListener listener = new MyGeneInactiveAccountListener(lConn, notifierKey, mapper, emailService, emailConfigFactory);
		listener.start();
	}

}

/**
 * Thread that connects to postgres and listens for the mygeneinactive notifier
 * @author lancefallon
 *
 */
class MyGeneInactiveAccountListener extends Thread {

	private Connection conn;
	private PGConnection pgconn;
	private AccountRowMapper mapper;
	private EmailService emailService;
	private EmailConfigFactory emailConfigFactory;

	MyGeneInactiveAccountListener(Connection conn, String notifierKey, AccountRowMapper mapper, 
			EmailService emailService, EmailConfigFactory emailConfigFactory) throws SQLException {
		this.conn = conn;
		this.pgconn = (PGConnection)conn;
		this.mapper = mapper;
		this.emailService = emailService;
		this.emailConfigFactory = emailConfigFactory;
		Statement stmt = conn.createStatement();
		
		//listen for the notifierKey specified in the properties file
		stmt.execute("LISTEN " + notifierKey);
		stmt.close();
	}

	public void run() {
		
		//just keep runnin', wilfred... just keep on runnin'
		while (true) {
			try {
				// issue a dummy query to contact the backend (required to receive any pending notifications) 
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1");
				rs.close();
				stmt.close();

				//load all inactive account notifications
				PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					for (int i=0; i<notifications.length; i++) {
						
						//each notification includes one paramater (the username of account that is one week from deletion).
						//run a query against the db to retrieve that account
						String sql = "SELECT * from uw.account where username = '" + notifications[i].getParameter() + "'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(sql);
						
						//cursor starts before first row, so moving to next
						rs.next();
						
						//map row to an account
						Account account = this.mapper.mapRow(rs);
						EmailConfig emailConfig = this.emailConfigFactory.generateForMyGeneInactiveAccountEmail(account);
						this.emailService.sendMail(emailConfig);
						System.out.println("Inactive notifier email sent to " + account.getEmail());
						
						//close result set and statement
						rs.close();
						stmt.close();
					}
				}

				// wait a while before checking again for new notifications
				Thread.sleep(500);
			} catch (SQLException sqle) {
				System.out.println("PostgresListener stopped!");
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				System.out.println("PostgresListener stopped!");
				ie.printStackTrace();
			}
		}
	}

}
