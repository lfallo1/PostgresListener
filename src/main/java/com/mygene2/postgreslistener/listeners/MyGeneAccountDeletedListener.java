package com.mygene2.postgreslistener.listeners;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;

import com.mygene2.postgreslistener.mapper.AccountRowMapper;
import com.mygene2.postgreslistener.model.Account;
import com.mygene2.postgreslistener.model.EmailConfig;
import com.mygene2.postgreslistener.service.EmailConfigFactory;
import com.mygene2.postgreslistener.service.EmailService;

/**
 * Thread that connects to postgres and listens for the mygeneinactive notifier
 * @author lancefallon
 *
 */
public class MyGeneAccountDeletedListener extends Thread {

	private Connection conn;
	private PGConnection pgconn;
	private AccountRowMapper mapper;
	private EmailService emailService;
	private EmailConfigFactory emailConfigFactory;

	public MyGeneAccountDeletedListener(Connection conn, String notifierKey, AccountRowMapper mapper, 
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
						stmt = conn.createStatement();
						String sql = "SELECT * from uw.account where username = '" + notifications[i].getParameter() + "'";
						rs = stmt.executeQuery(sql);
						
						//cursor starts before first row, so moving to next
						rs.next();
						
						//map row to an account
						Account account = this.mapper.mapRow(rs);
						EmailConfig emailConfig = this.emailConfigFactory.generateWithBasicAccountInfo(account, "velocity/accountDeleted.vm", "Account has been deleted");
						this.emailService.sendMail(emailConfig);
						System.out.println("Account deleted email sent to " + account.getEmail());
						
						//perform deletion
						stmt = conn.createStatement();
						sql = account.getIsClinician() ? "select uw.delete_clinician_account("+ account.getId() +");" : "select uw.delete_family_account("+ account.getId() +");";
						stmt.executeQuery(sql);
						
						//close result set and statement
						rs.close();
						stmt.close();
					}
				}

				// wait a while before checking again for new notifications
				Thread.sleep(500);
			} catch (SQLException sqle) {
				System.out.println("MyGeneAccountDeletedListener stopped!");
				sqle.printStackTrace();
			} catch (InterruptedException ie) {
				System.out.println("MyGeneAccountDeletedListener stopped!");
				ie.printStackTrace();
			}
		}
	}
}
