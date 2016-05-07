package com.mygene2.postgreslistener.service;

import java.text.SimpleDateFormat;

import com.mygene2.postgreslistener.model.Account;
import com.mygene2.postgreslistener.model.Config;
import com.mygene2.postgreslistener.model.EmailConfig;

/**
 * Helper service to generate EmailConfig objects for a specific type of email
 * @author lancefallon
 *
 */
public class EmailConfigFactory {

	private Config config;

	public EmailConfigFactory(Config config){
		this.config = config;
	}
	
	/**
	 * Generate for the inactive account reminder email
	 * @param account
	 * @return
	 */
	public EmailConfig generateForMyGeneInactiveAccountEmail(Account account){
		EmailConfig emailConfig = new EmailConfig();
		emailConfig.setBaseUrl(config.getBaseUrl());
		emailConfig.setFrom(config.getMygene2email());
		emailConfig.setMygene2email(config.getMygene2email());
		emailConfig.setName(account.getFirstName());
		emailConfig.setRecipient(account);
		emailConfig.setSubject("Account will be deleted in 7 days");
		emailConfig.setTemplate("velocity/pendingDeletion.vm");
		
		//ex. July 1, 2015
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
		emailConfig.setDateCreated(formatter.format(account.getDateCreated()));
		
		return emailConfig;
	}
	
}
