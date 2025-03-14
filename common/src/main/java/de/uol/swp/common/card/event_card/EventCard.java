package de.uol.swp.common.card.event_card;


import de.uol.swp.common.card.PlayerCard;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.player.Player;
import de.uol.swp.common.triggerable.ManualTriggerable;
import de.uol.swp.common.util.Color;
import lombok.Getter;
import lombok.Setter;

/**
 * Abstract class representing an event card in the game.
 * This class provides common properties and methods for all event cards.
 */
@Getter
public abstract class EventCard extends PlayerCard implements ManualTriggerable {

    protected String description;
    protected String title;
    protected final Color color = new Color(255, 165, 0);
    @Setter
    protected Game game;
    @Setter
    protected Player player;

    protected EventCard(String description, String title) {
        this.description = description;
        this.title = title;
    }

    public abstract String getEffectMessage();

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void initWithGame(final Game game) {
        this.game = game;
        this.player = game.findPlayer(this.player).orElseThrow();
        this.player.removeHandCard(this);
        this.player.addHandCard(this);
    }

    @Override
    public Player getAnsweringPlayer() {
        return player;
    }
}
