package com.mygene2.postgreslistener.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.mygene2.postgreslistener.model.Config;
import com.mygene2.postgreslistener.model.EmailConfig;
import com.sendgrid.SendGrid;
import com.sendgrid.SendGridException;

/**
 * Email Service
 *
 * @author lfallon
 *
 */
public class EmailService {

    private Config config;

    private VelocityEngine velocityEngine;
    
    public EmailService(Config config, VelocityEngine velocityEngine){
    	this.config = config;
    	this.velocityEngine = velocityEngine;
    }

    /**
     * send email
     *
     * @param emailConfig
     * @throws EmailException
     */
    public void sendMail(EmailConfig emailConfig) {
    	String apikey = this.config.decryptProperty(this.config.getApikey());
        SendGrid sendgrid = new SendGrid(apikey);
        
        SendGrid.Email email = new SendGrid.Email();
        try {

            //setup additional email config properties
            emailConfig.setBaseUrl(this.config.getBaseUrl());
            emailConfig.setFrom(this.config.getMygene2email());
            emailConfig.setMygene2email(this.config.getMygene2email());

            //set recipient / sender / subject
            email.addTo(emailConfig.getRecipient().getEmail());
            email.setFrom(emailConfig.getFrom());
            email.setSubject(emailConfig.getSubject());
            //attach the logo
            email.addAttachment("mygene2logo", new ClassPathResource("static/MyGene2Logo.PNG").getFile());
            email.addContentId("mygene2logo", "logo");

            //create the model for view
            Map<String, Object> model = new HashMap<>();
            model.put("emailConfig", emailConfig);

            //load appropriate template, and generate html using velocity view engine
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/emailHead.vm", "UTF-8", model);
            text += VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, emailConfig.getTemplate(), "UTF-8", model);
            text += VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/signature.vm", "UTF-8", model);
            
            email.setHtml(text);

            sendgrid.send(email);
        } catch (SendGridException | IOException ex) {
            ex.printStackTrace();
        }
    }

}
