package de.uol.swp.client.action;

import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.action.simple.car.CarAction;
import de.uol.swp.common.game.Game;
import org.greenrobot.eventbus.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

class ActionServiceTest {

    @Mock
    private ActionService actionService;

    @Mock
    private EventBus eventBus;

    @Mock
    private Game game;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        actionService = new ActionService(eventBus);
    }

    @Test
    @DisplayName("Checks whether an ActionRequest is sent via the EventBus")
    void executeActionOnCity() {
        actionService.sendAction(game, new CarAction());
        verify(eventBus).post(any(ActionRequest.class));
    }
}
