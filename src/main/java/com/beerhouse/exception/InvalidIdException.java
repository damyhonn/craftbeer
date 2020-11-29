package com.beerhouse.exception;

public class InvalidIdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4221023851487102785L;

	public InvalidIdException() {
		super();
	}

	public InvalidIdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidIdException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidIdException(String message) {
		super(message);
	}

	public InvalidIdException(Throwable cause) {
		super(cause);
	}

}
