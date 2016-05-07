package com.mygene2.postgreslistener.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import com.mygene2.postgreslistener.Driver;

/**
 * autowire properties
 * 
 * @author lancefallon
 *
 */
public class Config {

	private String apikey;

	private String emailHost;

	private String emailPort;

	private String baseUrl;

	private String mygene2email;

	public Config() {
		Properties props = new Properties();
		try {
			InputStream in = Driver.class.getClassLoader().getResourceAsStream(Driver.DRIVER_PROPERTIES);
			props.load(in);
			this.apikey = props.getProperty("mygene2.apikey");
			this.emailHost = props.getProperty("mygene2.emailHost");
			this.emailPort = props.getProperty("mygene2.emailPort");
			this.baseUrl = props.getProperty("mygene2.baseUrl");
			this.mygene2email = props.getProperty("mygene2.mygene2email");
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getApikey() {
		return apikey;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getMygene2email() {
		return mygene2email;
	}

	public String getEmailHost() {
		return emailHost;
	}

	public String getEmailPort() {
		return emailPort;
	}

	/**
	 * decrypt string
	 * 
	 * @param encryptedValue
	 * @return
	 */
	public String decryptProperty(String encryptedValue) {
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setAlgorithm("PBEWithMD5AndDES");
//		encryptor.setPassword(System.getenv("CAS_PBE_PASSWORD"));
		encryptor.setPassword("secret");
		return encryptor.decrypt(encryptedValue);
	}
}
