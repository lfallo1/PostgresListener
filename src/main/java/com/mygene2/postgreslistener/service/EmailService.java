package com.mygene2.postgreslistener.service;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.mygene2.postgreslistener.model.Config;
import com.mygene2.postgreslistener.model.EmailConfig;

/**
 * Email Service
 *
 * @author lfallon
 *
 */
public class EmailService {

    private Config config;

    private VelocityEngine velocityEngine;
    
    private JavaMailSenderImpl mailSender;
    
    public EmailService(Config config, VelocityEngine velocityEngine, JavaMailSenderImpl mailSender){
    	this.config = config;
    	this.velocityEngine = velocityEngine;
    	this.mailSender = mailSender;
    }

    /**
     * send email
     *
     * @param emailConfig
     * @throws EmailException
     */
    public void sendMail(EmailConfig emailConfig) {
        try {

            //setup additional email config properties
            emailConfig.setBaseUrl(this.config.getBaseUrl());
            emailConfig.setFrom(this.config.getMygene2email());
            emailConfig.setMygene2email(this.config.getMygene2email());

            //setup mime messagehelper
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");
            
            //set recipient / sender / subject
            helper.setTo(emailConfig.getRecipient().getEmail());
            helper.setFrom(emailConfig.getFrom());
            helper.setSubject(emailConfig.getSubject());

            //create the model for view
            Map<String, Object> model = new HashMap<>();
            model.put("emailConfig", emailConfig);

            //load appropriate template, and generate html using velocity view engine
            String text = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/emailHead.vm", "UTF-8", model);
            text += VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, emailConfig.getTemplate(), "UTF-8", model);
            text += VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "velocity/signature.vm", "UTF-8", model);
            
            helper.setText(text, true);
            
            //attach the logo
            helper.addInline("logo", new ClassPathResource("static/MyGene2Logo.PNG"));

            mailSender.send(mimeMessage);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
    }

}
