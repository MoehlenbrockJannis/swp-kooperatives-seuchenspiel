package de.uol.swp.common.lobby;

import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.UserPlayer;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        this.lobby.setId(1234);
    }

    @Test
    @DisplayName("Lobbys should be equal if names are equal")
    void equalsTest_equal() {
        final Lobby equalLobby = new LobbyDTO(lobby.getName(), users.iterator().next(), 2, 4);
        assertThat(lobby).isEqualTo(equalLobby);
    }

    @Test
    @DisplayName("Lobbys should not be equal if names are different")
    void equalsTest_notEqualLobby() {
        final Lobby notEqualLobby = new LobbyDTO("foo bar", users.iterator().next(), 2, 4);
        assertThat(lobby).isNotEqualTo(notEqualLobby);
    }

    @Test
    @DisplayName("Lobby should not be equal to objects of different types")
    void equalsTest_notEqualObject() {
        final Object object = new Object();
        assertThat(lobby).isNotEqualTo(object);
    }

    @Test
    @DisplayName("Hashcode should consist of a hashcode of the lobby's name")
    void hashCodeTest() {
        final int hashcode = Objects.hashCode("test");
        assertThat(lobby.hashCode()).isEqualTo(hashcode);
    }

    /**
     * This test check whether a lobby is created correctly
     *
     * If the variables are not set correctly the test fails
     *
     * @since 2019-10-08
     */
    @Test
    @DisplayName("Lobby should be created correctly")
    void createLobbyTest() {
        assertThat(lobby.getName()).isEqualTo("test");
        assertThat(lobby.getUsers()).hasSize(1);
        assertThat(lobby.getUsers()).contains(defaultUser);
        assertThat(lobby.getOwner()).isEqualTo(defaultUser);
        assertThat(lobby.getMinPlayers()).isEqualTo(2);
        assertThat(lobby.getMaxPlayers()).isEqualTo(4);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
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
    @DisplayName("Users should be able to join the lobby")
    void joinUserLobbyTest() {
        lobby.joinUser(users.get(0));
        assertThat(lobby.getUsers()).hasSize(2);
        assertThat(lobby.getUsers()).contains(users.get(0));
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.joinUser(users.get(0));
        assertThat(lobby.getUsers()).hasSize(2);
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.joinUser(users.get(1));
        assertThat(lobby.getUsers()).hasSize(3);
        assertThat(lobby.getUsers()).contains(users.get(1));
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
    @DisplayName("Users should be able to leave the lobby")
    void leaveUserLobbyTest() {
        users.forEach(lobby::joinUser);

        assertThat(lobby.getUsers()).hasSize(users.size() + 1);
        assertThat(lobby.getPlayers()).hasSameSizeAs(lobby.getUsers());

        lobby.leaveUser(users.get(5));
        assertThat(lobby.getUsers()).hasSize(users.size());
        assertThat(lobby.getUsers()).doesNotContain(users.get(5));
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
    @DisplayName("Owner should be able to leave the lobby and a new owner should be assigned")
    void removeOwnerFromLobbyTest() {
        users.forEach(lobby::joinUser);

        lobby.leaveUser(defaultUser);

        assertThat(lobby.getOwner()).isNotEqualTo(defaultUser);
        assertThat(users).anyMatch(user -> user.equals(lobby.getOwner()));
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
    @DisplayName("Owner should be updatable to a user in the lobby")
    void updateOwnerTest() {
        users.forEach(lobby::joinUser);

        lobby.updateOwner(users.get(6));
        assertThat(lobby.getOwner()).isEqualTo(users.get(6));

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
    @DisplayName("Lobby should not be allowed to be empty")
    void assureNonEmptyLobbyTest() {
        assertThrows(IllegalArgumentException.class, () -> lobby.leaveUser(defaultUser));
    }

    @Test
    @DisplayName("Lobby should correctly report if it contains a user")
    void containsUserTest() {
        assertThat(lobby.containsUser(defaultUser)).isTrue();
        assertThat(lobby.containsUser(users.iterator().next())).isFalse();
    }

    @Test
    @DisplayName("Players should be able to be added to the lobby")
    void addPlayerTest() {
        final Player player = new UserPlayer(users.iterator().next());

        lobby.addPlayer(player);
        assertThat(lobby.getPlayers()).contains(player);
    }

    @Test
    @DisplayName("Players should be able to be removed from the lobby")
    void removePlayerTest() {
        final Player player = new UserPlayer(users.iterator().next());

        lobby.addPlayer(player);
        assertThat(lobby.getPlayers()).contains(player);

        lobby.removePlayer(player);
        assertThat(lobby.getPlayers()).doesNotContain(player);
    }

    @Test
    @DisplayName("Lobby should return the correct player for a user in the lobby")
    void getPlayerForUser_userInLobby() {
        final Player player = new UserPlayer(defaultUser);

        assertThat(lobby.getPlayerForUser(defaultUser)).isEqualTo(player);
    }

    @Test
    @DisplayName("Lobby should return null for a user not in the lobby")
    void getPlayerForUser_userNotInLobby() {
        assertThat(lobby.getPlayerForUser(users.iterator().next())).isNull();
    }

    /**
     * This test checks if the status of a lobby is determined correctly
     *
     * The test fails if the status is not OPEN when the lobby is created
     * or if the status is not FULL when the lobby is full
     *
     */
    @Test
    @DisplayName("Lobby status should be determined correctly")
    void determineLobbyStatusTest() {
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);

        for (int i = 0; i < 3; i++) {
            lobby.joinUser(users.get(i));
        }

        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.FULL);

        lobby.leaveUser(users.get(0));

        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
    }

    @Test
    @DisplayName("Lobby status should be updatable")
    void setStatusTest() {
        ((LobbyDTO)lobby).setStatus(LobbyStatus.RUNNING);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.RUNNING);
    }

    @Test
    @DisplayName("ToString should return a non-empty string")
    void toStringTest() {
        assertThat(lobby.toString()).isNotEmpty();
    }

    @Test
    @DisplayName("User should not be removed if not in lobby")
    void leaveUserNotInLobbyTest() {
        lobby.joinUser(users.get(0));
        Set<User> initialUsers = lobby.getUsers();
        lobby.leaveUser(notInLobbyUser);
        assertThat(lobby.getUsers()).containsExactlyInAnyOrderElementsOf(initialUsers);
        assertThat(lobby.getOwner()).isEqualTo(defaultUser);
    }

    @Test
    @DisplayName("Owner should be updated when leaving and other users present")
    void leaveUserUpdateOwnerTest() {
        lobby.joinUser(users.get(0));
        lobby.leaveUser(defaultUser);
        assertThat(lobby.getOwner()).isEqualTo(users.get(0));
    }

    @Test
    @DisplayName("getPlayerForUser should return null for null user")
    void getPlayerForNullUserTest() {
        assertThat(lobby.getPlayerForUser(null)).isNull();
    }

    @Test
    @DisplayName("getPlayerForUser should return correct player for user in lobby")
    void getPlayerForUserInLobbyTest() {
        User testUser = users.get(0);
        lobby.joinUser(testUser);
        Player player = lobby.getPlayerForUser(testUser);
        assertThat(player).isNotNull();
        assertThat(player.containsUser(testUser)).isTrue();
    }

    @Test
    @DisplayName("Lobby status should be FULL when maximum players joined")
    void lobbyStatusFullTest() {
        for (int i = 0; i < 3; i++) {
            lobby.joinUser(users.get(i));
        }
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.FULL);
    }

    @Test
    @DisplayName("Lobby status should remain RUNNING even when full")
    void lobbyStatusRunningWhenFullTest() {
        for (int i = 0; i < 3; i++) {
            lobby.joinUser(users.get(i));
        }
        ((LobbyDTO)lobby).setStatus(LobbyStatus.RUNNING);
        assertThat(lobby.getStatus()).isEqualTo(LobbyStatus.RUNNING);
    }
}