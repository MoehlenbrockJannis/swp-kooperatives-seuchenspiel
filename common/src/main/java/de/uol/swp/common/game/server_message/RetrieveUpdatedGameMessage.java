package de.uol.swp.common.game.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RetrieveUpdatedGameMessage extends AbstractServerMessage {

    private Game game;
}
