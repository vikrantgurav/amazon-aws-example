package com.amazon.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazon.example.client.EmailClient;
import com.amazon.example.request.EmployeeRequest;
import com.amazon.example.response.Employee;
import com.amazon.example.response.MessageResponse;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.CreateTableResult;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/amazon/api")
public class AmazonController {

	public static final Logger logger = LoggerFactory.getLogger(AmazonController.class);

	@Autowired
	AmazonSQS sqs;

	@Autowired
	AmazonDynamoDB adb;
	
	@Value("${queue.url.name}")
	String queueUrl;

	@PostMapping(value = "/sendMessage")
	String sendMessage(@RequestBody String message) {

		return "SUCCESS";
	}
	
	@Autowired
	EmailClient emailClient;

	@GetMapping(value = "/createTable/{tableName}/{id}")
	String createTable(@PathVariable("tableName") String tableName, @PathVariable("id") String primaryKey) {
		CreateTableRequest createTableRequest = new CreateTableRequest()
				.withAttributeDefinitions(new AttributeDefinition(primaryKey, ScalarAttributeType.S))
				.withKeySchema(new KeySchemaElement(primaryKey, KeyType.HASH))
				.withProvisionedThroughput(new ProvisionedThroughput(2L, 2L)).withTableName(tableName);

		try {
			CreateTableResult ct = adb.createTable(createTableRequest);
			logger.info("Table : " + ct.getTableDescription().getTableName() + " with primary key "
					+ ct.getTableDescription().getKeySchema());

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "SUCCESS";
	}

	@PostMapping(value = "/putItem/{tableName}")
	String putItem(@PathVariable("tableName") String tableName, @RequestBody EmployeeRequest employeeRequest) {
		logger.info("{}", employeeRequest);
		HashMap<String, AttributeValue> items = new HashMap<>();
		items.put("empId", new AttributeValue(employeeRequest.getEmpId()));
		if (!StringUtils.isEmpty(employeeRequest.getName())) {
			items.put("name", new AttributeValue(employeeRequest.getName()));
		}
		if (!StringUtils.isEmpty(employeeRequest.getGender())) {
			items.put("gender", new AttributeValue(employeeRequest.getGender()));
		}

		try {
			adb.putItem(tableName, items);
		} catch (ResourceNotFoundException e) {
			logger.error("Error: The table {} can't be found.", tableName);
		} catch (AmazonServiceException e) {
			logger.error(e.getErrorMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "SUCCESS";
	}

	@GetMapping(value = "/getItem/{tableName}/{id}")
	Employee getItem(@PathVariable("tableName") String tableName, @PathVariable("id") String id) {
		logger.info("Key for get item: " + id + " for table: " + tableName);
		HashMap<String, AttributeValue> keys = new HashMap<>();
		keys.put("empId", new AttributeValue(id));
		Map<String, AttributeValue> response_item = adb.getItem(tableName, keys).getItem();
		Employee employee = null;
		if (response_item != null) {
			employee = new Employee();
			for (Entry<String, AttributeValue> e : response_item.entrySet()) {
				if (e.getKey().equals("empId") && !StringUtils.isEmpty(e.getValue())) {
					employee.setEmpId(e.getValue().getS());
				}
				if (e.getKey().equals("name") && !StringUtils.isEmpty(e.getValue())) {
					employee.setName(e.getValue().getS());
				}
				if (e.getKey().equals("gender") && !StringUtils.isEmpty(e.getValue())) {
					employee.setGender(e.getValue().getS());
				}
			}
		}
		
		if(employee != null) {
			logger.info("{}",employee);
		}
		return employee;
	}
	
	@GetMapping(value = "/sqs/getMessages")
	List<MessageResponse> getMessages() throws JsonParseException, JsonMappingException, IOException{
		logger.info("Getting all messages from the sqs queue");
		
		List<MessageResponse> messageResponses = null;
		ObjectMapper objectMapper = new ObjectMapper();
		logger.info("Queue url: {}",queueUrl);
		List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();
		
		if(messages!=null) {
			messageResponses = new ArrayList<>();
			for(Message m : messages) {
				String messageString = m.getBody();
				MessageResponse messageResponse = objectMapper.readValue(messageString, MessageResponse.class);
				logger.info("{}",messageResponse);
				messageResponses.add(messageResponse);
				emailClient.sendEmail(messageResponse);
				sqs.deleteMessage(queueUrl,m.getReceiptHandle());
			}
		}
		
		
		
		return messageResponses;
	}

}
