package com.mygene2.postgreslistener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

public class Driver {

	private static final String DATABASE_DRIVER_NAME = "org.postgresql.Driver";
	private static final String POSTGRES_NOTIFIER = "postgres.notifier";
	private static final String JDBC_PASSWORD = "jdbc.password";
	private static final String JDBC_USERNAME = "jdbc.username";
	private static final String JDBC_URL = "jdbc.url";
	private static final String DRIVER_PROPERTIES = "driver.properties";

	public static void main(String args[]) throws Exception {
		
		//load properties
		Properties props = new Properties();
		props.load(Driver.class.getClassLoader().getResourceAsStream(DRIVER_PROPERTIES));
		String url = props.getProperty(JDBC_URL);
		String username = props.getProperty(JDBC_USERNAME);
		String password = props.getProperty(JDBC_PASSWORD);
		String notifierKey = props.getProperty(POSTGRES_NOTIFIER);
		
		// Create connection, one for the notifier for the listener
		Class.forName(DATABASE_DRIVER_NAME);
		Connection lConn = DriverManager.getConnection(url, username, password);

		// Create thread on which notifications are received
		Listener listener = new Listener(lConn, notifierKey);
		listener.start();
	}

}

class Listener extends Thread {

	private Connection conn;
	private PGConnection pgconn;

	Listener(Connection conn, String notifierKey) throws SQLException {
		this.conn = conn;
		this.pgconn = (PGConnection)conn;
		Statement stmt = conn.createStatement();
		
		//listen for the notifierKey specified in the properties file
		stmt.execute("LISTEN " + notifierKey);
		stmt.close();
	}

	public void run() {
		while (true) {
			try {
				// issue a dummy query to contact the backend (required to receive any pending notifications) 
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT 1");
				rs.close();
				stmt.close();

				PGNotification notifications[] = pgconn.getNotifications();
				if (notifications != null) {
					for (int i=0; i<notifications.length; i++) {
						
						//each notification includes one paramater (the username of account that is one week from deletion).
						//run a query against the db to retrieve that account
						String sql = "SELECT * from uw.account where username = '" + notifications[i].getParameter() + "'";
						stmt = conn.createStatement();
						rs = stmt.executeQuery(sql);
						rs.next();
						
						//TODO replace this garbage with actual email sending logic
						String email = rs.getString("email");
						String username = rs.getString("username");
						System.out.println(email + " ("+ username +"), your account will be deleted in 7 days.");
						//end garbage
						
						//close connection and statement
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
