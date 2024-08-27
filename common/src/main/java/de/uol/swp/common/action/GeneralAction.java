package de.uol.swp.common.action;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;

public abstract class GeneralAction implements Action {

    private Player executingPlayer;
    private Game game;

    protected GeneralAction() {
        //Standard-Konstruktor; kann bei Bedarf erweitert werden
    }

    @Override
    public Player getExecutingPlayer() {
        return this.executingPlayer;
    }

    @Override
    public void setExecutingPlayer(Player player) {
        this.executingPlayer = player;
    }

    @Override
    public Game getGame() {
        return this.game;
    }

    @Override
    public void setGame(Game game) {
        this.game = game;
    }

    @Override
    public abstract boolean isAvailable();
}

