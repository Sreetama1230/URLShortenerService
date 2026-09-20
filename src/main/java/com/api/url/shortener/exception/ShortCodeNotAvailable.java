package com.api.url.shortener.exception;

public class ShortCodeNotAvailable  extends RuntimeException{
	public ShortCodeNotAvailable(String message) {
		super(message);
	}

}
