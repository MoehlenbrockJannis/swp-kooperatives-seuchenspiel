package de.uol.swp.server.game.store;

import de.uol.swp.common.game.Game;
import de.uol.swp.server.store.ContentStore;

import java.util.Optional;

/**
 * A store for managing games.
 */
public interface GameStore extends ContentStore {

    /**
     * Adds a game to the list of managed games.
     *
     * @param game The game to be added
     */
    void addGame(Game game);

    /**
     * Updates a game in the list of managed games.
     *
     * @param game The game to be updated
     */
    void updateGame(Game game);


    /**
     * Returns a game from the list of managed games.
     *
     * @param game The game to be returned
     * @return The game
     */
    Optional<Game> getGame(Game game);

    /**
     * Removes a game from the list of managed games.
     *
     * @param game The game to be removed
     */
    void removeGame(Game game);
}
