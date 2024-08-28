package de.uol.swp.client.main.event;

import de.uol.swp.common.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event used to show the main menu window
 *
 * <p>
 * In order to show the main menu window using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 * </p>
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.SceneManager
 * @since 2024-08-28
 *
 */
@Getter
@RequiredArgsConstructor
public class ReturnToMainMenuEvent {
    private final User user;
}