package de.uol.swp.common.game.request;


import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class AbstractGameRequestTest {

    @Test
    void abstractGameRequest_initializesWithCorrectAttributes() {
        Game mockGame = mock(Game.class);
        Player mockPlayer = mock(Player.class);
        AbstractGameRequest request = new AbstractGameRequest(mockGame, mockPlayer);
        assertThat(request.getGame()).isEqualTo(mockGame);
        assertThat(request.getPlayer()).isEqualTo(mockPlayer);
    }
}