package de.uol.swp.client.lobby.events;

import de.uol.swp.common.lobby.Lobby;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Event used to show the lobby window
 *
 * In order to show the lobby create window using this event, post an instance of it
 * onto the eventBus the SceneManager is subscribed to.
 *
 * @see de.uol.swp.client.SceneManager
 */
@Getter
@RequiredArgsConstructor
public class ShowLobbyViewEvent {
    private final Lobby lobby;
}