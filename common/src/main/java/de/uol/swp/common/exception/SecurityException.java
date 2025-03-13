package de.uol.swp.common.exception;

import java.io.Serial;

/**
 * Exception to state e.g. that a authorization is required
 */
public class SecurityException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -6908340347082873591L;

	/**
	 * Constructor
	 *
	 * @param message Text the Exception should contain
	 */
	public SecurityException(String message){
		super(message);
	}

}
