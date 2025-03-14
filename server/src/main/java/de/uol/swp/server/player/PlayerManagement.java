package de.uol.swp.server.player;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.Session;
import de.uol.swp.server.user.AuthenticationService;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Class that stores and handles access to the players and their sessions.
 */
@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlayerManagement {
    private final Map<Session, AIPlayer> aiPlayerSessions = new HashMap<>();

    private final AuthenticationService authenticationService;

    /**
     * Adds the given {@link Session} to {@link #aiPlayerSessions} with the given {@link AIPlayer} as association.
     *
     * @param player {@link AIPlayer} to add a {@link Session} for
     * @param session {@link Session} of the {@link AIPlayer}
     */
    public void addAIPlayerSession(final AIPlayer player, final Session session) {
        this.aiPlayerSessions.put(session, player);
    }

    /**
     * Finds a {@link Session} for the given {@link Player} by looking
     * in {@link #authenticationService} if given {@link Player} is a {@link UserPlayer} and
     * in {@link #aiPlayerSessions} if given {@link Player} is an {@link AIPlayer}.
     *
     * @param player {@link Player} to find {@link Session} for
     * @return {@link Optional} with {@link Session} for given {@link Player} or empty {@link Optional} if there is none
     */
    public Optional<Session> findSession(final Player player) {
        if (player instanceof UserPlayer userPlayer) {
            return authenticationService.getSession(userPlayer.getUser());
        } else if (player instanceof AIPlayer aiPlayer) {
            return aiPlayerSessions.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(aiPlayer))
                    .map(Map.Entry::getKey)
                    .findFirst();
        }
        return Optional.empty();
    }
}
