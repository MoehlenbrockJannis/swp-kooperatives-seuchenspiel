package de.uol.swp.common.card.event_card;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.GameDifficulty;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.LobbyDTO;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.player.turn.PlayerTurn;
import de.uol.swp.common.role.RoleAbility;
import de.uol.swp.common.role.RoleCard;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.util.Color;
import de.uol.swp.common.util.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static de.uol.swp.common.util.TestUtils.createPlayerTurn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AQuietNightEventCardTest {
    private AQuietNightEventCard aQuietNightEventCard;
    private Game game;
    private AIPlayer aiPlayer;
    private RoleCard roleCard1;
    private RoleAbility roleAbility;
    private User user;
    private Player userPlayer;

    @BeforeEach
    void setUp() {
        this.user = new UserDTO("Joerg", "333", "Joerg@mail.com");
        final Lobby lobby = new LobbyDTO("name",user);
        this.roleAbility = mock(RoleAbility.class);
        this.roleCard1 = new RoleCard("", new Color(), roleAbility);

        this.userPlayer = lobby.getPlayerForUser(user);
        userPlayer.setRole(roleCard1);

        aiPlayer = new AIPlayer("ai1");
        aiPlayer.setRole(roleCard1);

        lobby.addPlayer(this.aiPlayer);
        GameDifficulty difficulty = GameDifficulty.getDefault();

        game = new Game(lobby, TestUtils.createMapType(), new ArrayList<>(lobby.getPlayers()), List.of(), difficulty);

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