package de.uol.swp.common.message.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Encapsulates an Exception in a message object
 * 
 * @author Marco Grawunder
 * @since 2017-03-17
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ExceptionResponseMessage extends AbstractResponseMessage {
	private static final long serialVersionUID = -7739395567707525535L;

	private final String message;

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}
