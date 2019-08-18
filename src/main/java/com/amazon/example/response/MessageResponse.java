package com.amazon.example.response;

import java.io.Serializable;
import java.security.Timestamp;


import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("serial")
public class MessageResponse implements Serializable{

	@JsonProperty("Type")
	String type;
	
	@JsonProperty("MessageId")
	String messageId;
	
	@JsonProperty("TopicArn")
	String topicArn;
	
	@JsonProperty("Subject")
	String subject;
	
	@JsonProperty("Message")
	String message;
	
	@JsonProperty("Timestamp")
	String timestamp;
	
	@JsonProperty("SignatureVersion")
	String signatureVersion;
	
	@JsonProperty("Signature")
	String signature;
	
	@JsonProperty("SigningCertURL")
	String signingCertURL;
	
	@JsonProperty("UnsubscribeURL")
	String unsubscribeUrl;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTopicArn() {
		return topicArn;
	}

	public void setTopicArn(String topicArn) {
		this.topicArn = topicArn;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSignatureVersion() {
		return signatureVersion;
	}

	public void setSignatureVersion(String signatureVersion) {
		this.signatureVersion = signatureVersion;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getSigningCertURL() {
		return signingCertURL;
	}

	public void setSigningCertURL(String signingCertURL) {
		this.signingCertURL = signingCertURL;
	}

	public String getUnsubscribeUrl() {
		return unsubscribeUrl;
	}

	public void setUnsubscribeUrl(String unsubscribeUrl) {
		this.unsubscribeUrl = unsubscribeUrl;
	}

	@Override
	public String toString() {
		return "MessageResponse [type=" + type + ", messageId=" + messageId + ", topicArn=" + topicArn + ", subject="
				+ subject + ", message=" + message + ", timestamp=" + timestamp + ", signatureVersion="
				+ signatureVersion + ", signature=" + signature + ", signingCertURL=" + signingCertURL
				+ ", unsubscribeUrl=" + unsubscribeUrl + "]";
	}
	
	
}
