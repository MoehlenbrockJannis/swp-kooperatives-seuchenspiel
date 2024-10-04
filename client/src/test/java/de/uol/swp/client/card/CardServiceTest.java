package de.uol.swp.client.card;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.card.request.DiscardPlayerCardRequest;
import de.uol.swp.common.card.request.DrawPlayerCardRequest;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class CardServiceTest extends EventBusBasedTest {

    private CardService cardService;

    @BeforeEach
    void setUp() {
        cardService = new CardService(getBus());
    }

    @Test
    void sendDrawPlayerCardRequest() throws InterruptedException {
        Game game = mock(Game.class);
        Player player = mock(Player.class);

        cardService.sendDrawPlayerCardRequest(game, player);
        waitForLock();

        assertTrue(event instanceof DrawPlayerCardRequest);

        final DrawPlayerCardRequest drawPlayerCardRequest = (DrawPlayerCardRequest) event;
        assertEquals(drawPlayerCardRequest.getGame(), game);
        assertEquals(drawPlayerCardRequest.getPlayer(), player);
        assertThat(drawPlayerCardRequest.getGame()).isEqualTo(game);
        assertThat(drawPlayerCardRequest.getPlayer()).isEqualTo(player);

    }

    @Test
    void sendDiscardPlayerCardRequest() throws InterruptedException {
        Game game = mock(Game.class);
        Player player = mock(Player.class);
        PlayerCard playerCard = mock(PlayerCard.class);

        cardService.sendDiscardPlayerCardRequest(game, player, playerCard);
        waitForLock();

        assertThat(event).isInstanceOf(DiscardPlayerCardRequest.class);

        final DiscardPlayerCardRequest discardPlayerCardRequest = (DiscardPlayerCardRequest) event;
        assertThat(discardPlayerCardRequest.getGame()).isEqualTo(game);
        assertThat(discardPlayerCardRequest.getPlayer()).isEqualTo(player);
        assertThat(discardPlayerCardRequest.getCard()).isEqualTo(playerCard);
    }

    @Subscribe
    public void onEvent(DrawPlayerCardRequest drawPlayerCardRequest) {
        handleEvent(drawPlayerCardRequest);
    }

    @Subscribe
    public void onEvent(DiscardPlayerCardRequest discardPlayerCardRequest) {
        handleEvent(discardPlayerCardRequest);
    }


}
