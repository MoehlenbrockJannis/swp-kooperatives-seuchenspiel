package de.uol.swp.common.message.response;

import de.uol.swp.common.game.Game;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * An abstract response message that is tied to a specific game.
 * <p>
 * This base class is used for response messages related to actions or events
 * within the context of a {@link Game}. Subclasses can extend this to implement
 * specific game-related responses.
 * </p>
 */
@AllArgsConstructor
@Getter
public abstract class AbstractGameResponse extends AbstractResponseMessage {

    protected final Game game;
}
