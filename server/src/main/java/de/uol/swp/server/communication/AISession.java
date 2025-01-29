package de.uol.swp.server.communication;

import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.request.LoginRequest;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

/**
 * Class used to store AI Players in an identifiable way
 *
 * @see de.uol.swp.server.usermanagement.AuthenticationService#onLoginRequest(LoginRequest)
 * @see Session
 * @author Silas van Thiel
 * @since 2025-01-27
 */
public class AISession implements Session {

	private final String sessionId;
    @Getter
    private final AIPlayer aiPlayer;

	/**
	 * private Constructor
	 *
	 * @param aiPlayer the AI Player connected to the session
	 */
	private AISession(AIPlayer aiPlayer) {
		synchronized (AISession.class) {
			this.sessionId = String.valueOf(UUID.randomUUID());
            this.aiPlayer = aiPlayer;
		}
	}

	/**
	 * Builder for the UUIDSession
	 * <p>
	 * Builder exposed to every class in the server, used since the constructor is private
	 *
	 * @param aiPlayer the AI Player connected to the session
	 * @return a new UUIDSession object for the user
	 */
	public static Session createAISession(AIPlayer aiPlayer) {
		return new AISession(aiPlayer);
	}

    @Override
	public String getSessionId() {
		return sessionId;
	}

    @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AISession session = (AISession) o;
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
