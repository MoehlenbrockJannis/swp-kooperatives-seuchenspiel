package de.uol.swp.common.card.event_card;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.common.util.TestUtils.createPlayerTurn;
import static org.assertj.core.api.Assertions.assertThat;

class AQuietNightEventCardTest {
    private AQuietNightEventCard aQuietNightEventCard;
    private Game game;

    @BeforeEach
    void setUp() {
        final Lobby lobby = new LobbyDTO("name", new UserDTO("user", "", ""), 2, 4);
        lobby.addPlayer(new AIPlayer("ai"));

        game = new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of());

        aQuietNightEventCard = new AQuietNightEventCard();
        aQuietNightEventCard.setGame(game);
    }

    @Test
    void trigger() {
        final PlayerTurn playerTurn = createPlayerTurn(
                game,
                game.getCurrentPlayer(),
                0,
                0,
                4
        );
        game.addPlayerTurn(playerTurn);

        assertThat(playerTurn.getNumberOfInfectionCardsToDraw())
                .isPositive();

        aQuietNightEventCard.trigger();

        assertThat(playerTurn.getNumberOfInfectionCardsToDraw())
                .isZero();
    }
}