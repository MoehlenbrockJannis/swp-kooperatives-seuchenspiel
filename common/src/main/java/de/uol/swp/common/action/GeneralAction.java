package de.uol.swp.common.action;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public abstract class GeneralAction implements Action {

    private Player executingPlayer;
    private Game game;

    @Override
    public abstract boolean isAvailable();
}

