package de.uol.swp.server.approvable;

import com.google.inject.Inject;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * The {@link ApprovableService} class handles the receiving and sending of messages regarding approvables.
 *
 * @author Tom Weelborg
 */
public class ApprovableService extends AbstractService {
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus the EvenBus used throughout the server
     * @param lobbyService {@link LobbyService} to send messages to lobby members
     * @since 2019-10-08
     */
    @Inject
    public ApprovableService(final EventBus bus, final LobbyService lobbyService) {
        super(bus);
        this.lobbyService = lobbyService;
    }

    /**
     * Handles incoming {@link ApprovableRequest} and creates an {@link ApprovableServerMessage} with the same {@link ApprovableMessageStatus}.
     *
     * @param approvableRequest the {@link ApprovableRequest} detected on the {@link EventBus}
     */
    @Subscribe
    public void onApprovableRequest(final ApprovableRequest approvableRequest) {
        final Approvable approvable = approvableRequest.getApprovable();
        final Lobby lobbyOfApprovable = approvable.getGame().getLobby();
        final ApprovableServerMessage approvableServerMessage = new ApprovableServerMessage(
                approvableRequest.getStatus(),
                approvable
        );
        lobbyService.sendToAllInLobby(lobbyOfApprovable, approvableServerMessage);
    }
}
