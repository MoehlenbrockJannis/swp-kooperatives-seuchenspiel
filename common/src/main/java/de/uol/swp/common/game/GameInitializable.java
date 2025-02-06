package de.uol.swp.common.game;

public interface GameInitializable {
    /**
     * Initializes this object with given {@link Game} by setting all relevant attributes to current version in {@link Game}
     *
     * @param game {@link Game} with current state
     */
    void initWithGame(Game game);
}
