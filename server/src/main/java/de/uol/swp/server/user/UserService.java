package de.uol.swp.server.user;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.message.response.ResponseMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.RegisterUserRequest;
import de.uol.swp.common.user.response.RegisterUserExceptionResponse;
import de.uol.swp.common.user.response.RegisterUserSuccessResponse;
import de.uol.swp.server.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Mapping vom event bus calls to user management calls
 *
 * @see de.uol.swp.server.AbstractService
 */
@Singleton
public class UserService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(UserService.class);

    private final UserManagement userManagement;

    /**
     * Constructor
     *
     * @param eventBus the EventBus used throughout the entire server (injected)
     * @param userManagement object of the UserManagement to use
     * @see de.uol.swp.server.user.UserManagement
     */
    @Inject
    public UserService(EventBus eventBus, UserManagement userManagement) {
        super(eventBus);
        this.userManagement = userManagement;
    }

    /**
     * Handles RegisterUserRequests found on the EventBus
     * <p>
     * If a RegisterUserRequest is detected on the EventBus, this method is called.
     * It tries to create a new user via the UserManagement. If this succeeds a
     * RegistrationSuccessfulResponse is posted on the EventBus otherwise a RegistrationExceptionMessage
     * gets posted there.
     *
     * @param msg The RegisterUserRequest found on the EventBus
     * @see de.uol.swp.server.user.UserManagement#createUser(User)
     * @see RegisterUserRequest
     * @see RegisterUserSuccessResponse
     * @see RegisterUserExceptionResponse
     */
    @Subscribe
    public void onRegisterUserRequest(RegisterUserRequest msg) {
        if (LOG.isDebugEnabled()){
            LOG.debug("Got new registration message with {}", msg.getUser());
        }
        ResponseMessage returnMessage;
        try {
            userManagement.createUser(msg.getUser());
            returnMessage = new RegisterUserSuccessResponse();
        }catch (Exception e){
            LOG.error(e);
            returnMessage = new RegisterUserExceptionResponse("Nutzer konnte nicht erstellt werden "+msg.getUser()+" "+e.getMessage());
        }
        msg.getMessageContext().ifPresent(returnMessage::setMessageContext);
        post(returnMessage);
    }
}
