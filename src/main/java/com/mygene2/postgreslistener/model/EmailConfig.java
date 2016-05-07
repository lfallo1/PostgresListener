package com.mygene2.postgreslistener.model;

/**
 * DTO for velocity templates
 * @author lancefallon
 *
 */
public class EmailConfig {
	private Account recipient;
	private String from;
	private String url;
	private String subject;
	private String template;
	private String name;
	private String baseUrl;
	private String mygene2email;
	private String dateCreated;

	public EmailConfig() {
	}

	public EmailConfig(Account recipient, String from, String url, String subject, String template,
			String name, String baseUrl, String mygene2email, String dateCreated) {
		this.recipient = recipient;
		this.from = from;
		this.url = url;
		this.subject = subject;
		this.template = template;
		this.name = name;
		this.baseUrl = baseUrl;
		this.mygene2email = mygene2email;
		this.dateCreated = dateCreated;
	}

	public Account getRecipient() {
		return recipient;
	}

	public void setRecipient(Account recipient) {
		this.recipient = recipient;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getMygene2email() {
		return mygene2email;
	}

	public void setMygene2email(String mygene2email) {
		this.mygene2email = mygene2email;
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

}
