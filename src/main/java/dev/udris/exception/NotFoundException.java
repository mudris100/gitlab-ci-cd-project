package dev.udris.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException{

	private static final long serialVersionUID = 9199895090094264382L;

	public NotFoundException(String entity, String field, Object value) {		
		super(entity + " with " + field + " '" + value + "' was not found");

	}
	
	

}
