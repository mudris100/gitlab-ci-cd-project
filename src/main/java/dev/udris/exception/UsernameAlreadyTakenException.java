package dev.udris.exception;

public class UsernameAlreadyTakenException extends RuntimeException {

	private static final long serialVersionUID = 4325031021715741916L;

	public UsernameAlreadyTakenException(String message) {
		super(message);
	}

}
