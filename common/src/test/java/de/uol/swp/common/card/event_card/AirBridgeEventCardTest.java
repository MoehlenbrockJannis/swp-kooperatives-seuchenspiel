package de.uol.swp.common.card.event_card;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AirBridgeEventCardTest {
    private AirBridgeEventCard airBridgeEventCard;
    private Game game;
    private Player player1;
    private Player player2;
    private Field startingField;
    private Field notStartingField;

    @BeforeEach
    void setUp() {
        final User user = new UserDTO("user", "", "");
        final Lobby lobby = new LobbyDTO("name", user);
        player1 = lobby.getPlayerForUser(user);

        player2 = new AIPlayer("ai");
        lobby.addPlayer(player2);
        GameDifficulty difficulty = GameDifficulty.getDefault();

        game = new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);

        startingField = game.getMap().getStartingField();
        for (int i = 0; notStartingField == null || notStartingField.equals(startingField); i++) {
            notStartingField = game.getFields().get(i);
        }

        airBridgeEventCard = new AirBridgeEventCard();
        airBridgeEventCard.setGame(game);
        airBridgeEventCard.setPlayer(player1);
        airBridgeEventCard.setTargetPlayer(player2);
        airBridgeEventCard.setTargetField(notStartingField);
    }

    @Test
    @DisplayName("Should move the target player if approved")
    void trigger_approved() {
        assertThat(player2.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(startingField);

        airBridgeEventCard.approve();

        airBridgeEventCard.trigger();

        assertThat(player2.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(notStartingField);
    }

    @Test
    @DisplayName("Should do nothing if not approved")
    void trigger_notApproved() {
        assertThat(player2.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(startingField);

        airBridgeEventCard.trigger();

        assertThat(player2.getCurrentField())
                .usingRecursiveComparison()
                .isEqualTo(startingField);
    }
}