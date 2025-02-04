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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class MainMemoryBasedLobbyStoreTest {

    private MainMemoryBasedLobbyStore lobbyStore;
    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");
    final Lobby defaultLobby = new LobbyDTO("TestLobby", defaultUser);
    final Lobby defaultLobby2 = new LobbyDTO("TestLobby2", defaultUser);

    @BeforeEach
    void setUp() {
        lobbyStore = new MainMemoryBasedLobbyStore();
    }

    @Test
    @DisplayName("Add a new lobby successfully")
    void addLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getName())).contains(defaultLobby);
    }

    @Test
    @DisplayName("Throw exception when adding an existing lobby")
    void addExistingLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThatThrownBy(() -> lobbyStore.addLobby(defaultLobby)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Remove an existing lobby successfully")
    void removeLobby() {

        lobbyStore.addLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getName())).contains(defaultLobby);

        lobbyStore.removeLobby(defaultLobby);

        assertThat(lobbyStore.getLobby(defaultLobby.getName())).isEmpty();
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
        Optional<Lobby> retrievedLobby = lobbyStore.getLobby(defaultLobby.getName());

        assertThat(retrievedLobby).isPresent().contains(defaultLobby);
    }

    @Test
    @DisplayName("Return empty when getting a non-existing lobby")
    void getNonExistingLobby() {
        Optional<Lobby> retrievedLobby = lobbyStore.getLobby(defaultLobby.getName());
        assertThat(retrievedLobby).isEmpty();
    }

    @Test
    @DisplayName("Get all lobbies successfully")
    void getAllLobbies() {

        lobbyStore.addLobby(defaultLobby);
        lobbyStore.addLobby(defaultLobby2);
        assertThat(lobbyStore.getAllLobbies()).isEqualTo(List.of(defaultLobby2, defaultLobby));
    }

    @Test
    @DisplayName("Update an existing lobby successfully")
    void updateLobby() {
        lobbyStore.addLobby(defaultLobby);

        assertThat(defaultLobby.getStatus()).isEqualTo(LobbyStatus.OPEN);
        defaultLobby.setStatus(LobbyStatus.RUNNING);
        lobbyStore.updateLobby(defaultLobby);

        Lobby updatedLobby = lobbyStore.getLobby(defaultLobby.getName()).orElseThrow();
        assertThat(updatedLobby.getStatus()).isEqualTo(LobbyStatus.RUNNING);

    }

    @Test
    @DisplayName("Throw exception when updating a non-existing lobby")
    void updateNonExistingLobby() {

        assertThatThrownBy(() -> lobbyStore.updateLobby(defaultLobby)).isInstanceOf(IllegalArgumentException.class);

    }
}