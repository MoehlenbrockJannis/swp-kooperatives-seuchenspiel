package de.uol.swp.common.user.response;

import de.uol.swp.common.message.response.ExceptionResponseMessage;

/**
 * This exception is thrown if something went wrong during the registration process.
 * e.g.: The username is already taken
 */
public class RegisterUserExceptionResponse extends ExceptionResponseMessage {
    /**
     * Constructor
     *
     * @param message String containing the reason why the registration failed
     */
    public RegisterUserExceptionResponse(final String message){
        super(message);
    }
}
