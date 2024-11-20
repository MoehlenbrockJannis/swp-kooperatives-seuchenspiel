package de.uol.swp.common.message.response;

import de.uol.swp.common.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class AbstractGameResponse extends AbstractResponseMessage {

    protected final Game game;
}
