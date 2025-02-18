package de.uol.swp.client.card;

import de.uol.swp.common.card.PlayerCard;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;

/**
 * A {@link HBox} that can have a {@link PlayerCard} assigned to it.
 * This class is used for the {@link de.uol.swp.client.game.PlayerPanePresenter}.
 */
@Getter
public class PlayerCardHBox extends HBox {
    @Setter
    private PlayerCard playerCard;
    private Color backgroundColor;

    /**
     * Sets the background color of the {@link PlayerCardHBox} to the given {@link Color}.
     *
     * @param color The {@link Color} that will be used as the background color.
     */
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.setBackground(new Background(new BackgroundFill(color, null, null)));
    }
}
