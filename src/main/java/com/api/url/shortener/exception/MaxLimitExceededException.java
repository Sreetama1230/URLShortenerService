package com.api.url.shortener.exception;

public class MaxLimitExceededException  extends RuntimeException {

	public MaxLimitExceededException(String message){
		super(message);
	}
}
