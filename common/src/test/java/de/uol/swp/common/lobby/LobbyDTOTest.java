package de.uol.swp.common.lobby;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Class for the UserDTO
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
class LobbyDTOTest {

    private static final User defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");
    private static final User notInLobbyUser = new UserDTO("no", "marco", "no@grawunder.de");

    private static final int NO_USERS = 10;
    private static final List<UserDTO> users;

    private Lobby lobby;

    static {
        users = new ArrayList<>();
        for (int i = 0; i < NO_USERS; i++) {
            users.add(new UserDTO("marco" + i, "marco" + i, "marco" + i + "@grawunder.de"));
        }
        Collections.sort(users);
    }

    @BeforeEach
    void setup() {
        this.lobby = new LobbyDTO("test", defaultUser, 2, 4);
    }

    @Test
    @DisplayName("Lobbys should be equal if names are equal")
    void equalsTest_equal() {
        final Lobby equalLobby = new LobbyDTO(lobby.getName(), users.iterator().next(), 2, 4);

        assertThat(lobby.equals(equalLobby))
                .isTrue();
    }

    @Test
    @DisplayName("Lobbys should be not equal if names are equal")
    void equalsTest_notEqualLobby() {
        final Lobby notEqualLobby = new LobbyDTO("foo bar", users.iterator().next() , 2, 4);

        assertThat(lobby.equals(notEqualLobby))
                .isFalse();
    }

    @Test
    @DisplayName("Lobbys should be not equal if names are equal")
    void equalsTest_notEqualObject() {
        final Object object = new Object();

        assertThat(lobby.equals(object))
                .isFalse();
    }

    @Test
    @DisplayName("Hashcode should consist of a hashcode of the lobby's name")
    void hashCodeTest() {
        final int hashcode = Objects.hashCode("test");

        assertThat(lobby.hashCode())
                .isEqualTo(hashcode);
    }

    /**
     * This test check whether a lobby is created correctly
     *
     * If the variables are not set correctly the test fails
     *
     * @since 2019-10-08
     */
    @Test
    void createLobbyTest() {
        assertEquals("test", lobby.getName());
        assertEquals(1, lobby.getUsers().size());
        assertEquals(defaultUser, lobby.getUsers().iterator().next());

    }

    /**
     * This test check whether a user can join a lobby
     *
     * The test fails if the size of the user list of the lobby does not get bigger
     * or a user who joined is not in the list.
     *
     * @since 2019-10-08
     */
    @Test
    void joinUserLobbyTest() {
        lobby.joinUser(users.get(0));
        assertEquals(2,lobby.getUsers().size());
        assertTrue(lobby.getUsers().contains(users.get(0)));
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.joinUser(users.get(0));
        assertEquals(2, lobby.getUsers().size());
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.joinUser(users.get(1));
        assertEquals(3,lobby.getUsers().size());
        assertTrue(lobby.getUsers().contains(users.get(1)));
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());
    }

    /**
     * This test check whether a user can leave a lobby
     *
     * The test fails if the size of the user list of the lobby does not get smaller
     * or the user who left is still in the list.
     *
     * @since 2019-10-08
     */
    @Test
    void leaveUserLobbyTest() {
        users.forEach(lobby::joinUser);

        assertEquals(lobby.getUsers().size(), users.size() + 1);
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.leaveUser(users.get(5));
        assertEquals(lobby.getUsers().size(), users.size() + 1 - 1);
        assertFalse(lobby.getUsers().contains(users.get(5)));
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());
    }

    /**
     * Test to check if the owner can leave the Lobby correctly
     *
     * This test fails if the owner field is not updated if the owner leaves the
     * lobby or if he still is in the user list of the lobby.
     *
     * @since 2019-10-08
     */
    @Test
    void removeOwnerFromLobbyTest() {
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertNotEquals(defaultUser, lobby.getOwner() );
        assertTrue(users.contains(lobby.getOwner()));

    }

    /**
     * This checks if the owner of a lobby can be updated and if he has have joined
     * the lobby
     *
     * This test fails if the owner cannot be updated or does not have to be joined
     *
     * @since 2019-10-08
     */
    @Test
    void updateOwnerTest() {
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertEquals(lobby.getOwner(), users.get(6));

        assertThrows(IllegalArgumentException.class, () -> lobby.updateOwner(notInLobbyUser));
    }

    /**
     * This test check whether a lobby can be empty
     *
     * If the leaveUser function does not throw an Exception the test fails
     *
     * @since 2019-10-08
     */
    @Test
    void assureNonEmptyLobbyTest() {
        assertThrows(IllegalArgumentException.class, () -> lobby.leaveUser(defaultUser));
    }

    @Test
    void containsUserTest() {
        assertThat(lobby.containsUser(defaultUser))
                .isTrue();
        assertThat(lobby.containsUser(users.iterator().next()))
                .isFalse();
    }

    @Test
    void addPlayerTest() {
        final Player player = new UserPlayer(users.iterator().next());

        lobby.addPlayer(player);
        assertThat(lobby.getPlayers())
                .contains(player);
    }

    @Test
    void removePlayer() {
        final Player player = new UserPlayer(users.iterator().next());

        lobby.addPlayer(player);
        assertThat(lobby.getPlayers())
                .contains(player);

        lobby.removePlayer(player);
        assertThat(lobby.getPlayers())
                .doesNotContain(player);
    }

    @Test
    void getPlayerForUser_userInLobby() {
        final Player player = new UserPlayer(defaultUser);

        assertThat(lobby.getPlayerForUser(defaultUser))
                .isEqualTo(player);
    }

    @Test
    void getPlayerForUser_userNotInLobby() {
        assertThat(lobby.getPlayerForUser(users.iterator().next()))
                .isNull();
    }

    /**
     * This test checks if the status of a lobby is determined correctly
     *
     * The test fails if the status is not OPEN when the lobby is created
     * or if the status is not FULL when the lobby is full
     *
     */
    @Test
    void determineLobbyStatusTest() {
        assertEquals(LobbyStatus.OPEN, lobby.getStatus());

        for (int i = 0; i < 3; i++) {
            lobby.joinUser(users.get(i));
        }

        assertEquals(LobbyStatus.FULL, lobby.getStatus());

        lobby.leaveUser(users.get(0));

        assertEquals(LobbyStatus.OPEN, lobby.getStatus());
    }
}
