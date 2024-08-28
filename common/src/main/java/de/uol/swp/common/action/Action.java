package de.uol.swp.common.action;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

public interface Action extends Command {

    Player getExecutingPlayer();

    void setExecutingPlayer(Player player);

    Game getGame();

    void setGame(Game game);

    boolean isAvailable();
}
