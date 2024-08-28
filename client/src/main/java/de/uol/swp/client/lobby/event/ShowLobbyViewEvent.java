package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.Lobby;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event used to show the lobby window
 *
 * In order to show the lobby create window using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 * @author Niklas Wrobel
 * @see de.uol.swp.client.SceneManager
 * @since 2024-08-28
 */
@Getter
@RequiredArgsConstructor
public class ShowLobbyViewEvent {
    private final Lobby lobby;
}