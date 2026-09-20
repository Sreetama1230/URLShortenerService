package com.api.url.shortener.exception;

public class URLExpirationException  extends RuntimeException{

	public URLExpirationException(String message) {
		super(message);
	}
}
