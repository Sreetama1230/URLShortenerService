package com.api.url.shortener.dto;

import org.springframework.http.HttpStatus;

public class ErrorObject {

	private String message;
	private HttpStatus statusCode;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public HttpStatus getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}
	public ErrorObject() {
		super();
	}
	public ErrorObject(String message, HttpStatus statusCode) {
		super();
		this.message = message;
		this.statusCode = statusCode;
	}
	
	
	
}
