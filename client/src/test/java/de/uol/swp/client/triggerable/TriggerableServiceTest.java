package de.uol.swp.client.triggerable;

import de.uol.swp.client.EventBusBasedTest;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.player.AIPlayer;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.Triggerable;
import de.uol.swp.common.triggerable.request.TriggerableRequest;
import org.greenrobot.eventbus.Subscribe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TriggerableServiceTest extends EventBusBasedTest {
    private TriggerableService triggerableService;

    private Triggerable triggerable;
    private Message cause;
    private Player returningPlayer;

    @BeforeEach
    void setUp() {
        triggerableService = new TriggerableService(getBus());

        triggerable = new Triggerable() {
            @Override
            public Game getGame() {
                return null;
            }

            @Override
            public void setGame(Game game) {

            }

            @Override
            public Player getPlayer() {
                return null;
            }

            @Override
            public void setPlayer(Player player) {

            }

            @Override
            public void trigger() {

            }

            @Override
            public void initWithGame(Game game) {

            }
        };
        cause = null;
        returningPlayer = new AIPlayer("ai");
    }

    @Test
    void sendManualTriggerable() throws InterruptedException {
        triggerableService.sendManualTriggerable(triggerable, cause, returningPlayer);

        waitForLock();

        assertThat(event)
                .isInstanceOf(TriggerableRequest.class);
        final TriggerableRequest actual = (TriggerableRequest) event;
        final TriggerableRequest expected = new TriggerableRequest(triggerable, cause, returningPlayer);
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Subscribe
    public void onEvent(final TriggerableRequest triggerableRequest) {
        handleEvent(triggerableRequest);
    }
}