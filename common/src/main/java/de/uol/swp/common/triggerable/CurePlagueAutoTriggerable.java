package de.uol.swp.common.triggerable;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.player.Player;
import lombok.Getter;
import lombok.Setter;

/**
 * Auto-triggerable implementation that automatically cures all plague cubes of a specific type
 * when a player with the "Arzt" (Doctor) role is on a field with an antidote marker.
 * When triggered, removes all plague cubes of the field's plague type.
 */
@Getter
@Setter
public class CurePlagueAutoTriggerable implements AutoTriggerable {

    private Game game;
    private Player player;

    /**
     * Checks if triggering condition is met.
     * Triggers when a player with "Arzt" role is on a field that has an antidote marker for its plague.
     *
     * @return true if the auto-triggerable should activate, false otherwise
     */
    @Override
    public boolean isTriggered() {
        Plague currentPlague = player.getCurrentField().getPlague();
        return game.hasAntidoteMarkerForPlague(currentPlague);
    }

    /**
     * Performs the triggerable action.
     * Removes all plague cubes of the field's plague type from the player's current field.
     */
    @Override
    public void trigger() {
        Field field = player.getCurrentField();
        Plague plague = field.getPlague();
        field.removeAllPlagueCubes(plague, field, game, true);
    }

    /**
     * Initializes this triggerable with the game instance.
     * Sets the game reference and retrieves the player object from the game.
     *
     * @param game The game instance to initialize with
     */
    @Override
    public void initWithGame(Game game) {
        this.game = game;
        this.player = game.findPlayer(this.player).orElseThrow();
    }
}
