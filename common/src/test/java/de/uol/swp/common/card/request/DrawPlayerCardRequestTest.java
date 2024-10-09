package de.uol.swp.common.card.request;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DrawPlayerCardRequestTest {

    @Test
    void drawPlayerCardRequest_initializesWithCorrectAttributes() {
        Game mockGame = mock(Game.class);
        Player mockPlayer = mock(Player.class);
        DrawPlayerCardRequest request = new DrawPlayerCardRequest(mockGame, mockPlayer);
        assertThat(request.getGame()).isEqualTo(mockGame);
        assertThat(request.getPlayer()).isEqualTo(mockPlayer);
    }

}