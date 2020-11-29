package com.beerhouse.exception;

public class BeerAlreadyExistsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 373824092000905089L;

	public BeerAlreadyExistsException() {
		super();
	}

	public BeerAlreadyExistsException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BeerAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public BeerAlreadyExistsException(String message) {
		super(message);
	}

	public BeerAlreadyExistsException(Throwable cause) {
		super(cause);
	}
	
}
