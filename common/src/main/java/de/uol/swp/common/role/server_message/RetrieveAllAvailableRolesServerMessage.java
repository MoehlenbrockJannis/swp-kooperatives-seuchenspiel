package de.uol.swp.common.role.server_message;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.server_message.AbstractUserLobbyServerMessage;
import de.uol.swp.common.role.RoleCard;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Represents a server message containing all available role cards for a lobby.
 * <p>
 * This class extends {@link AbstractUserLobbyServerMessage} and is used to send information
 * about available role cards in a specific lobby from the server to the client.
 * </p>
 *
 * @see AbstractUserLobbyServerMessage
 * @see RoleCard
 * @see Lobby
 */
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@NoArgsConstructor
public class RetrieveAllAvailableRolesServerMessage extends AbstractUserLobbyServerMessage {

    private Set<RoleCard> roleCards;

    /**
     * Constructs a new RetrieveAllAvailableRolesServerMessage with the specified lobby and available role cards.
     *
     * @param lobby                 the lobby for which the role cards are available
     * @param availableRolesInLobby the set of role cards available in the lobby
     */
    public RetrieveAllAvailableRolesServerMessage(Lobby lobby, Set<RoleCard> availableRolesInLobby) {
        setLobby(lobby);
        this.roleCards = availableRolesInLobby;
    }
}