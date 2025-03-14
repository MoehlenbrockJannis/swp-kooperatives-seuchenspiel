package de.uol.swp.server.communication;

import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.LoginRequest;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * Class used to store connected clients and Users in an identifiable way
 *
 * @see de.uol.swp.server.user.AuthenticationService#onLoginRequest(LoginRequest)
 * @see de.uol.swp.common.user.Session
 */
public class UUIDSession implements Session {

	private final String sessionId;
    @Getter
    private final User user;

	/**
	 * private Constructor
	 *
	 * @param user the user connected to the session
	 */
	private UUIDSession(User user) {
		synchronized (UUIDSession.class) {
			this.sessionId = String.valueOf(UUID.randomUUID());
            this.user = user;
		}
	}

	/**
	 * Builder for the UUIDSession
	 * <p>
	 * Builder exposed to every class in the server, used since the constructor is private
	 *
	 * @param user the user connected to the session
	 * @return a new UUIDSession object for the user
	 */
	public static Session create(User user) {
		return new UUIDSession(user);
	}

    @Override
	public String getSessionId() {
		return sessionId;
	}

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		UUIDSession session = (UUIDSession) o;
		return Objects.equals(sessionId, session.sessionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(sessionId);
	}

	@Override
	public String toString() {
		return "SessionId: "+sessionId;
	}
	
}
