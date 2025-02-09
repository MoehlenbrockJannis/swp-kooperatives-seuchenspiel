package de.uol.swp.server.approvable;

import com.google.inject.Inject;
import de.uol.swp.common.approvable.Approvable;
import de.uol.swp.common.approvable.ApprovableMessageStatus;
import de.uol.swp.common.approvable.request.ApprovableRequest;
import de.uol.swp.common.approvable.server_message.ApprovableServerMessage;
import de.uol.swp.common.card.event_card.EventCard;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.chat.message.SystemLobbyMessageServerInternalMessage;
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
     * Handles incoming {@link ApprovableRequest} and
     * creates an {@link ApprovableServerMessage} with the same {@link ApprovableMessageStatus}.
     *
     * @param approvableRequest the {@link ApprovableRequest} detected on the {@link EventBus}
     */
    @Subscribe
    public void onApprovableRequest(final ApprovableRequest approvableRequest) {
        final ApprovableMessageStatus status = approvableRequest.getStatus();
        final Approvable approvable = approvableRequest.getApprovable();
        final Lobby lobbyOfApprovable = approvable.getGame().getLobby();
        final ApprovableServerMessage approvableServerMessage = new ApprovableServerMessage(
                status,
                approvable,
                approvableRequest.getOnApproved(),
                approvableRequest.getOnApprovedPlayer(),
                approvableRequest.getOnRejected(),
                approvableRequest.getOnRejectedPlayer()
        );
        lobbyService.sendToAllInLobby(lobbyOfApprovable, approvableServerMessage);

        sendAppropriateChatMessage(approvable, status, lobbyOfApprovable);
    }

    /**
     * Sends a chat message depending on the status to all players in given {@link Lobby}
     * if the given {@link Approvable} is not an {@link EventCard}.
     *
     * @param approvable {@link Approvable} for which to send a chat message
     * @param status status of the {@link Approvable}
     * @param lobbyOfApprovable target {@link Lobby} of the message
     */
    private void sendAppropriateChatMessage(final Approvable approvable,
                                            final ApprovableMessageStatus status,
                                            final Lobby lobbyOfApprovable) {
        if (approvable instanceof EventCard) {
            return;
        }

        switch (status) {
            case APPROVED -> sendApprovableChatMessage(lobbyOfApprovable, approvable.getApprovedMessage());
            case REJECTED -> sendApprovableChatMessage(lobbyOfApprovable, approvable.getRejectedMessage());
        }
    }

    /**
     * Sends a chat message update for an {@link Approvable}.
     *
     * @param lobby {@link Lobby} of the {@link Approvable} to send a message to
     * @param message {@link String} representing the message to send to the {@link Lobby}
     */
    private void sendApprovableChatMessage(final Lobby lobby, final String message) {
        SystemLobbyMessageServerInternalMessage systemLobbyMessageServerInternalMessage =
                new SystemLobbyMessageServerInternalMessage(message, lobby);
        post(systemLobbyMessageServerInternalMessage);
    }
}
