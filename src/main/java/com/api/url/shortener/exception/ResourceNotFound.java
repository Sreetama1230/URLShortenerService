package com.api.url.shortener.exception;

public class ResourceNotFound extends RuntimeException {

	public ResourceNotFound(String message) {
		super(message);
	}
}
