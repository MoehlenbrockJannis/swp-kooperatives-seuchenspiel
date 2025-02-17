package de.uol.swp.common.card.event_card;

import de.uol.swp.common.card.InfectionCard;
import de.uol.swp.common.card.stack.CardStack;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ToughPopulationEventCardTest {
    private ToughPopulationEventCard toughPopulationEventCard;
    private Game game;

    @BeforeEach
    void setUp() {
        final Lobby lobby = new LobbyDTO("name", new UserDTO("user", "", ""));
        lobby.addPlayer(new AIPlayer("ai"));
        GameDifficulty difficulty = GameDifficulty.getDefault();

        game = new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);

        toughPopulationEventCard = new ToughPopulationEventCard();
        toughPopulationEventCard.setGame(game);
    }

    @Test
    @DisplayName("Test if the trigger method removes the infection card from the infection discard stack")
    void trigger() {
        InfectionCard infectionCard = mock(InfectionCard.class);
        CardStack<InfectionCard> infectionDiscardStack = game.getInfectionDiscardStack();
        infectionDiscardStack.push(infectionCard);
        toughPopulationEventCard.setInfectionCard(infectionCard);

        assertThat(infectionDiscardStack).contains(infectionCard);

        toughPopulationEventCard.trigger();

        assertThat(infectionDiscardStack).doesNotContain(infectionCard);
    }
}