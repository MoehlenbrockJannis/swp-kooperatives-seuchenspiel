package de.uol.swp.common.game.response;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.message.AbstractServerMessage;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GameCreatedResponse extends AbstractServerMessage {
    private Game game;
}
