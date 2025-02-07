package de.uol.swp.server.lobby.store;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.lobby.LobbyStatus;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MainMemoryBasedLobbyStoreTest {

    private MainMemoryBasedLobbyStore lobbyStore;
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser, 2, 4);
    final Lobby defaultLobby2 = new LobbyDTO("TestLobby2", defaultUser, 2, 4);

    @BeforeEach
    void setUp() {
        lobbyStore = new MainMemoryBasedLobbyStore();
    }

    @Test
    @DisplayName("Add a new lobby successfully")
    void addLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getId())).contains(defaultLobby);
    }

    @Test
    @DisplayName("Throw exception when adding an existing lobby")
    void addExistingLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getId())).contains(defaultLobby);

        assertThatThrownBy(() -> lobbyStore.addLobby(defaultLobby)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Remove an existing lobby successfully")
    void removeLobby() {

        lobbyStore.addLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getId())).contains(defaultLobby);

        lobbyStore.removeLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getId())).isEmpty();
    }

    @Test
    @DisplayName("Throw exception when removing a non-existing lobby")
    void removeNonExistingLobby() {

        assertThatThrownBy(() -> lobbyStore.removeLobby(defaultLobby)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Get an existing lobby successfully")
    void getLobby() {

        lobbyStore.addLobby(defaultLobby);
        Optional<Lobby> retrievedLobby = lobbyStore.getLobby(defaultLobby.getId());

        assertThat(retrievedLobby).isPresent().contains(defaultLobby);
    }

    @Test
    @DisplayName("Return empty when getting a non-existing lobby")
    void getNonExistingLobby() {
        Optional<Lobby> retrievedLobby = lobbyStore.getLobby(defaultLobby.getId());
        assertThat(retrievedLobby).isEmpty();
    }

    @Test
    @DisplayName("Get all lobbies successfully")
    void getAllLobbies() {

        lobbyStore.addLobby(defaultLobby);
        lobbyStore.addLobby(defaultLobby2);
        List<Lobby> allLobbies = lobbyStore.getAllLobbies();
        assertThat(allLobbies).isNotNull().contains(defaultLobby, defaultLobby2);
    }

    @Test
    @DisplayName("Update an existing lobby successfully")
    void updateLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThat(defaultLobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
        defaultLobby.setStatus(LobbyStatus.RUNNING);
        lobbyStore.updateLobby(defaultLobby);

        Lobby updatedLobby = lobbyStore.getLobby(defaultLobby.getId()).orElseThrow();
        assertThat(updatedLobby.getStatus()).isEqualTo(LobbyStatus.RUNNING);

    }

    @Test
    @DisplayName("Throw exception when updating a non-existing lobby")
    void updateNonExistingLobby() {

        assertThatThrownBy(() -> lobbyStore.updateLobby(defaultLobby)).isInstanceOf(IllegalArgumentException.class);

    }
}