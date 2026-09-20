package com.api.url.shortener.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.api.url.shortener.dto.ErrorObject;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleShortCodeNotAvailable(ShortCodeNotAvailable shortCodeNotAvailable){
		
		return new ResponseEntity<ErrorObject>
		(new ErrorObject(shortCodeNotAvailable.getMessage(),
				HttpStatus.CONFLICT) , HttpStatus.CONFLICT);
	}
	
	
	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleResourceNotFound(ResourceNotFound resourceNotFound){
		
		return new ResponseEntity<ErrorObject>
		(new ErrorObject(resourceNotFound.getMessage(),
				HttpStatus.NOT_FOUND) , HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler
	public ResponseEntity<ErrorObject> handleURLExpirationException(URLExpirationException urlExpirationException){
		
		return new ResponseEntity<ErrorObject>
		(new ErrorObject(urlExpirationException.getMessage(),
				HttpStatus.GONE) , HttpStatus.GONE);
	}
}
