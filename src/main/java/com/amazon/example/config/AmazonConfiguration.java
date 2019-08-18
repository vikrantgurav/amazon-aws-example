package com.amazon.example.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;


@Configuration
public class AmazonConfiguration {

	@Bean
	public AmazonSQS sqs() {
		final AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		return sqs;
	}
	
	@Bean
	public AmazonDynamoDB adb() {
		final AmazonDynamoDB adb = AmazonDynamoDBClientBuilder.defaultClient();
		return adb;
	}
	
	
	@Bean
	public Properties props() {
		Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		return prop;
	}
}
