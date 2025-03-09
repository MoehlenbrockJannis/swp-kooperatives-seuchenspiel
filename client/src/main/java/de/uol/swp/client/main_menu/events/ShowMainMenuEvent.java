package de.uol.swp.client.main_menu.events;

import de.uol.swp.common.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event used to show the main menu
 *
 * <p>
 * In order to show the main menu using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 * The main menu will be opened with the name of the {@link #user} in its title.
 * </p>
 *
 * @author Tom Weelborg
 * @see de.uol.swp.client.SceneManager
 * @since 2024-08-24
 */
@Getter
@RequiredArgsConstructor
public class ShowMainMenuEvent {
    private final User user;
}
