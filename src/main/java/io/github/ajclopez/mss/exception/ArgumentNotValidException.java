package io.github.ajclopez.mss.exception;

/**
 * 
 * Exception to be thrown when an argument fails.
 *
 */
public class ArgumentNotValidException extends RuntimeException {

	private static final long serialVersionUID = -6604378815230212111L;

	public ArgumentNotValidException(String message) {
		super(message);
	}
	
}
