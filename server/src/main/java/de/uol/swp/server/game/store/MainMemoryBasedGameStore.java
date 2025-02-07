package de.uol.swp.server.game.store;

import de.uol.swp.common.game.Game;
import de.uol.swp.server.store.AbstractStore;
import de.uol.swp.server.store.MainMemoryBasedStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A game store that stores games in the main memory.
 */
public class MainMemoryBasedGameStore extends AbstractStore implements GameStore, MainMemoryBasedStore {
    private final Map<Integer, Game> games = new HashMap<>();

    @Override
    public void addGame(Game game) {
        if (doesGameExist(game)) {
            throw new IllegalArgumentException("Game with id " + game.getId() + " already exists");
        }
        game.setId(generateUniqueId());
        games.put(game.getId(), game);
    }

    @Override
    public void updateGame(Game game) {
        throwGameNotFoundException(game);
        games.put(game.getId(), game);
    }

    @Override
    public Optional<Game> getGame(Game game) {
        return Optional.ofNullable(games.get(game.getId()));
    }

    @Override
    public void removeGame(Game game) {
        throwGameNotFoundException(game);
        games.remove(game.getId());
    }

    @Override
    public Set<Integer> getIds() {
        return games.keySet();
    }

    /**
     * Checks if a game exists.
     *
     * @param game The game to be checked
     */
    private boolean doesGameExist(Game game) {
        return games.containsKey(game.getId());
    }

    /**
     * Throws an exception if a game does not exist.
     *
     * @param game The game to be checked
     */
    private void throwGameNotFoundException(Game game) {
        if (!doesGameExist(game)) {
            throw new IllegalArgumentException("Game with id " + game.getId() + " does not exist");
        }
    }
}
