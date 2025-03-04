package de.uol.swp.server.player;

import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.communication.AISession;
import de.uol.swp.server.communication.UUIDSession;
import de.uol.swp.server.usermanagement.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PlayerManagementTest {
    private PlayerManagement playerManagement;
    private AuthenticationService authenticationService;

    private AIPlayer aiPlayer;
    private Session aiSession;
    private UserPlayer userPlayer;
    private Session userSession;

    @BeforeEach
    void setUp() {
        authenticationService = mock();

        playerManagement = new PlayerManagement(authenticationService);

        aiPlayer = new AIPlayer("ai");
        aiSession = AISession.createAISession(aiPlayer);

        final User user = new UserDTO("user", "", "");
        userPlayer = new UserPlayer(user);
        userSession = UUIDSession.create(user);
    }

    @Test
    void addAIPlayerSession() {
        assertThat(playerManagement.findSession(aiPlayer))
                .isEmpty();

        playerManagement.addAIPlayerSession(aiPlayer, aiSession);

        assertThat(playerManagement.findSession(aiPlayer))
                .isNotEmpty()
                .usingFieldByFieldValueComparator()
                .contains(aiSession);
    }

    @RequiredArgsConstructor
    abstract class findSession {
        abstract Player getPlayer();

        abstract Session getSession();

        abstract void foundSetup();

        @Test
        void found() {
            foundSetup();

            assertThat(playerManagement.findSession(getPlayer()))
                    .usingFieldByFieldValueComparator()
                    .contains(getSession());
        }

        @Test
        void notFound() {
            assertThat(playerManagement.findSession(getPlayer()))
                    .isEmpty();
        }
    }

    @Nested
    class findSession_UserPlayer extends findSession {
        @Override
        Player getPlayer() {
            return userPlayer;
        }

        @Override
        Session getSession() {
            return userSession;
        }

        @Override
        void foundSetup() {
            when(authenticationService.getSession(userPlayer.getUser()))
                    .thenReturn(Optional.of(userSession));
        }
    }

    @Nested
    class findSession_AIPlayer extends findSession {
        @Override
        Player getPlayer() {
            return aiPlayer;
        }

        @Override
        Session getSession() {
            return aiSession;
        }

        @Override
        void foundSetup() {
            playerManagement.addAIPlayerSession(aiPlayer, aiSession);
        }
    }

    @Nested
    class findSession_otherPlayer extends findSession {
        @Override
        Player getPlayer() {
            return new Player() {
                @Override
                public String getName() {
                    return "name";
                }

                @Override
                public boolean containsUser(User user) {
                    return false;
                }
            };
        }

        @Override
        Session getSession() {
            return null;
        }

        @Override
        void foundSetup() {
            // ignore
        }

        @Override
        void found() {
            // ignore
        }
    }
}