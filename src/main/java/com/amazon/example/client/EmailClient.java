package com.amazon.example.client;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazon.example.response.MessageResponse;

@Component
public class EmailClient {

	@Value("${username.value}")
	private String username;

	@Value("${password.value}")
	private String password;
	
	@Value("${recipient.email.value}")
	private String recipientEmail;

	@Autowired
	Properties props;

	public static final Logger logger = LoggerFactory.getLogger(EmailClient.class);
	
	public void sendEmail(MessageResponse messageResponse) {

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject(messageResponse.getSubject());
			message.setText(messageResponse.getMessage());

			Transport.send(message);

			logger.info("Email sent to: "+recipientEmail);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
