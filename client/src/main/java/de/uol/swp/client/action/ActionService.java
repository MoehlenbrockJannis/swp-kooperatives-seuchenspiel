package de.uol.swp.client.action;

import com.google.inject.Inject;
import de.uol.swp.common.action.Action;
import de.uol.swp.common.action.request.ActionRequest;
import de.uol.swp.common.game.Game;
import lombok.RequiredArgsConstructor;
import org.greenrobot.eventbus.EventBus;

/**
 * The ActionService class handles sending action requests within the game.
 * It utilizes an EventBus to post actions that need to be executed.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ActionService {

    private final EventBus eventBus;

    /**
     * Sends an action request for the specified game and action.
     *
     * @param game   the game for which the action is to be sent
     * @param action the action to be executed
     */
    public void sendAction(Game game, Action action) {
        ActionRequest actionRequest = new ActionRequest(game, action);
        eventBus.post(actionRequest);
    }
}
