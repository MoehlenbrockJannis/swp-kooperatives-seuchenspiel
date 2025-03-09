package de.uol.swp.common.game.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RetrieveUpdatedGameServerMessage extends AbstractServerMessage {

    private Game game;
}
