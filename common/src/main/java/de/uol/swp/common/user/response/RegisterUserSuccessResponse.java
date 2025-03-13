package de.uol.swp.common.user.response;

import de.uol.swp.common.message.response.AbstractResponseMessage;

/**
 * A response, that the user registration was successful
 *
 * This response is only sent to clients that previously sent a RegisterUserRequest
 * that was executed successfully, otherwise an ExceptionMessage would be sent.
 */
public class RegisterUserSuccessResponse extends AbstractResponseMessage {

}
