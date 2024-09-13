package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.map.MapType;
import de.uol.swp.common.plague.Plague;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the game
 */
public class GameManagement {

    /**
     * Creates a game
     *
     * @param lobby    The lobby from which the game is to be created
     * @param mapType  The mapType of the game
     * @param plagues  The plagues of the game
     * @return The created game
     */
    public Game createGame(Lobby lobby, MapType mapType, List<Plague> plagues) {
        return new Game(lobby, mapType, new ArrayList<>(lobby.getPlayers()), plagues);
    }
}
