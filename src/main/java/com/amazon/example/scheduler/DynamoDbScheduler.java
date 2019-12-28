package com.amazon.example.scheduler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;

@Component
public class DynamoDbScheduler {

	public static final Logger logger = LoggerFactory.getLogger(DynamoDbScheduler.class);

	@Autowired
	AmazonSQS sqs;

	@Autowired
	AmazonDynamoDB adb;

	@Value("${queue.url.name}")
	String queueUrl;

	@Value("${table.name}")
	String tableName;

	@Value("${telegram.url}")
	String urlString;

	@Value("${telegram.apiToken}")
	private String apiToken;

	@Value("${telegram.chatId}")
	private String chatId;
	
	@Scheduled(fixedDelay = (300 * 1000))
	public void getMedicineMessages() throws IOException {
		logger.info("Getting medicines in the queue");
		logger.info("Queue url: {}", queueUrl);
		logger.info("Table name: " + tableName);
		logger.info("Telegram token : {}" , apiToken);
		List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
		for (Message m : messages) {
			logger.info("Message body: " + m.getBody());
			HashMap<String, AttributeValue> keys = new HashMap<>();
			keys.put("medicine_id", new AttributeValue(m.getBody()));
			Map<String, AttributeValue> response_item = adb.getItem(tableName, keys).getItem();
			if (null != response_item) {
				StringBuilder txtMessage = new StringBuilder();
				String name = null;
				String dose = null;
				String freq = null;
				for (Entry<String, AttributeValue> e : response_item.entrySet()) {
					if (e.getKey().equalsIgnoreCase("name") && !StringUtils.isEmpty(e.getValue())) {
						name = e.getValue().getS();
					}
					if (e.getKey().equalsIgnoreCase("doses") && !StringUtils.isEmpty(e.getValue())) {
						dose = e.getValue().getS();
					}
					if (e.getKey().equalsIgnoreCase("frequency") && !StringUtils.isEmpty(e.getValue())) {
						freq = e.getValue().getN();
					}
				}

				if (!StringUtils.isEmpty(name) && !StringUtils.isEmpty(dose) && !StringUtils.isEmpty(freq)) {
					txtMessage.append("Dear Patient \n");
					txtMessage.append("Please find doses for your medicine: " + name);
					txtMessage.append("\n Dose: \n " + dose);
					txtMessage.append("\n Frequency: " + freq);
				}

				if (!txtMessage.toString().equals("")) {
					logger.info("Text message: " + txtMessage.toString());
					URL url;
					String telegramURL = urlString;
					telegramURL = String.format(telegramURL, apiToken, chatId);
					logger.info("Telegram URL: {}", telegramURL);
					url = new URL(telegramURL + URLEncoder.encode(txtMessage.toString(), "UTF-8"));
					logger.info("URL: {}",url);
					URLConnection conn = url.openConnection();
					StringBuilder sb = new StringBuilder();
					InputStream is = new BufferedInputStream(conn.getInputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(is));
					String inputLine = "";
					while ((inputLine = br.readLine()) != null) {
						sb.append(inputLine);
					}
					String response = sb.toString();
					logger.info("Telegram API reponse : {}", response);
				}
			}
		}

	}
}
