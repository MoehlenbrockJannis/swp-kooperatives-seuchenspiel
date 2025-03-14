package de.uol.swp.common.message.response;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;

/**
 * Encapsulates an Exception in a message object
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
public class ExceptionResponseMessage extends AbstractResponseMessage {
	@Serial
	private static final long serialVersionUID = -7739395567707525535L;

	private final String message;

	@Override
	public String toString() {
		return getClass().getSimpleName() + ": " + message;
	}
}
