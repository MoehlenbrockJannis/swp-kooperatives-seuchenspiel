package de.uol.swp.common.game.server_message;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.server_message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateGameServerMessage extends AbstractServerMessage {
    private Game game;
}
