package com.mygene2.postgreslistener;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

import com.mygene2.postgreslistener.listeners.MyGeneAccountDeletedListener;
import com.mygene2.postgreslistener.listeners.MyGeneInactiveAccountListener;
import com.mygene2.postgreslistener.mapper.AccountRowMapper;
import com.mygene2.postgreslistener.model.Config;
import com.mygene2.postgreslistener.service.EmailConfigFactory;
import com.mygene2.postgreslistener.service.EmailService;
import com.mygene2.postgreslistener.service.MyGeneVelocityEngine;

public class Driver {

	private static final String DATABASE_DRIVER_NAME = "org.postgresql.Driver";
	private static final String NOTIFIER_ACCOUNT_INACTIVE = "postgres.accountinactivenotifier";
	private static final String NOTIFIER_ACCOUNT_DELETED = "postgres.accountdeletednotifier";
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
		String accountInactiveNotifierKey = props.getProperty(NOTIFIER_ACCOUNT_INACTIVE);
		String accountDeletedNotifierKey = props.getProperty(NOTIFIER_ACCOUNT_DELETED);
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
		MyGeneInactiveAccountListener inactiveAccountListener = new MyGeneInactiveAccountListener(lConn, accountInactiveNotifierKey, mapper, emailService, emailConfigFactory);
		inactiveAccountListener.start();
		
		MyGeneAccountDeletedListener listenerAccountDeleted = new MyGeneAccountDeletedListener(lConn, accountDeletedNotifierKey, mapper, emailService, emailConfigFactory);
		listenerAccountDeleted.start();
	}

}
