package de.uol.swp.server.user;


import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.LoginRequest;
import de.uol.swp.common.user.request.LogoutRequest;
import de.uol.swp.common.user.request.RetrieveAllOnlineUsersRequest;
import de.uol.swp.common.user.response.RetrieveAllOnlineUsersResponse;
import de.uol.swp.common.user.server_message.LogoutServerMessage;
import de.uol.swp.common.user.server_message.RetrieveAllOnlineUsersServerMessage;
import de.uol.swp.server.EventBusBasedTest;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import de.uol.swp.server.user.store.MainMemoryBasedUserStore;
import de.uol.swp.server.user.store.UserStore;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class AuthenticationServiceTest extends EventBusBasedTest {

    final User user = new UserDTO("name", "password", "email@test.de");
    final User user2 = new UserDTO("name2", "password2", "email@test.de2");
    final User user3 = new UserDTO("name3", "password3", "email@test.de3");

    final UserStore userStore = new MainMemoryBasedUserStore();
    final UserManagement userManagement = new UserManagement(userStore);
    final AuthenticationService authService = new AuthenticationService(getBus(), userManagement);

    @Subscribe
    public void onEvent(ClientAuthorizedMessage e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(ServerExceptionMessage e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(LogoutServerMessage e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(RetrieveAllOnlineUsersResponse e) {
        handleEvent(e);
    }

    @Subscribe
    public void onEvent(RetrieveAllOnlineUsersServerMessage e) {
        handleEvent(e);
    }

    @Test
    void loginTest() throws InterruptedException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword());
        postAndWait(loginRequest);
        assertTrue(userManagement.isLoggedIn(user));
        // is message send
        assertInstanceOf(ClientAuthorizedMessage.class, event);
        userManagement.dropUser(user);
    }

    @Test
    void loginTestFail() throws InterruptedException {
        userManagement.createUser(user);
        final LoginRequest loginRequest = new LoginRequest(user.getUsername(), user.getPassword() + "äüö");
        postAndWait(loginRequest);

        assertFalse(userManagement.isLoggedIn(user));
        assertInstanceOf(ServerExceptionMessage.class, event);
        userManagement.dropUser(user);
    }

    @Test
    void logoutTest() throws InterruptedException {
        loginUser(user);
        Optional<Session> session = authService.getSession(user);

        assertTrue(session.isPresent());
        final LogoutRequest logoutRequest = new LogoutRequest();
        logoutRequest.setSession(session.get());

        postAndWait(logoutRequest);

        assertFalse(userManagement.isLoggedIn(user));
        assertFalse(authService.getSession(user).isPresent());
    }

    private void loginUser(User userToLogin) {
        userManagement.createUser(userToLogin);
        final LoginRequest loginRequest = new LoginRequest(userToLogin.getUsername(), userToLogin.getPassword());
        post(loginRequest);

        assertTrue(userManagement.isLoggedIn(userToLogin));
        userManagement.dropUser(userToLogin);
    }

    @Test
    void loggedInUsers() throws InterruptedException {
        loginUser(user);

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        postAndWait(request);
        assertInstanceOf(RetrieveAllOnlineUsersServerMessage.class, event);

        assertEquals(1, ((RetrieveAllOnlineUsersServerMessage) event).getUsers().size());
        assertEquals(user, ((RetrieveAllOnlineUsersServerMessage) event).getUsers().get(0));

    }

    @Test
    void twoLoggedInUsers() throws InterruptedException {
        List<User> users = new ArrayList<>();
        users.add(user);
        users.add(user2);
        Collections.sort(users);

        users.forEach(this::loginUser);

        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        postAndWait(request);

        assertInstanceOf(RetrieveAllOnlineUsersServerMessage.class, event);

        List<User> returnedUsers = new ArrayList<>(((RetrieveAllOnlineUsersServerMessage) event).getUsers());

        assertEquals(2,returnedUsers.size());

        Collections.sort(returnedUsers);
        assertEquals(returnedUsers, users);

    }


    @Test
    void loggedInUsersEmpty() throws InterruptedException {
        RetrieveAllOnlineUsersRequest request = new RetrieveAllOnlineUsersRequest();
        postAndWait(request);
        assertInstanceOf(RetrieveAllOnlineUsersServerMessage.class, event);

        assertTrue(((RetrieveAllOnlineUsersServerMessage) event).getUsers().isEmpty());

    }

    @Test
    void getSessionsForUsersTest() {
        loginUser(user);
        loginUser(user2);
        loginUser(user3);
        Set<User> users = new TreeSet<>();
        users.add(user);
        users.add(user2);
        users.add(user3);


        Optional<Session> session1 = authService.getSession(user);
        Optional<Session> session2 = authService.getSession(user2);
        Optional<Session> session3 = authService.getSession(user2);

        assertTrue(session1.isPresent());
        assertTrue(session2.isPresent());
        assertTrue(session3.isPresent());

        List<Session> sessions = authService.getSessions(users);

        assertEquals(3, sessions.size());
        assertTrue(sessions.contains(session1.get()));
        assertTrue(sessions.contains(session2.get()));
        assertTrue(sessions.contains(session3.get()));

    }

}