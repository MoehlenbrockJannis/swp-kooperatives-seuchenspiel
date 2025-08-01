package de.uol.swp.client.user;

import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.*;
import org.greenrobot.eventbus.EventBus;

/**
 * This class is used to hide the communication details
 * implements de.uol.common.user.UserService
 *
 * @see ClientUserService
 *
 */
public class UserService implements ClientUserService {

	private final EventBus bus;

	/**
	 * Constructor
	 *
	 * @param bus The  EventBus set in ClientModule
	 * @see de.uol.swp.client.di.ClientModule
	 */
	@Inject
	public UserService(EventBus bus) {
		this.bus = bus;
	}

	/**
	 * Posts a login request to the EventBus
	 *
	 * @param username the name of the user
	 * @param password the password of the user
	 */
	@Override
	public void login(String username, String password){
		LoginRequest msg = new LoginRequest(username, password);
		bus.post(msg);
	}

	@Override
	public void logout(User user){
		LogoutRequest msg = new LogoutRequest(user);
		bus.post(msg);
	}

	@Override
	public void createUser(User user) {
		RegisterUserRequest request = new RegisterUserRequest(user);
		bus.post(request);
	}

	@Override
	public void updateUser(User user) {
		UpdateUserRequest request = new UpdateUserRequest(user);
		bus.post(request);
	}


	@Override
	public void retrieveAllUsers() {
		RetrieveAllOnlineUsersRequest cmd = new RetrieveAllOnlineUsersRequest();
		bus.post(cmd);
	}
}
