package de.uol.swp.client.card;

import de.uol.swp.common.card.Card;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.AllArgsConstructor;

/**
 * A class representing a popup for displaying card information.
 */
@AllArgsConstructor
public class CardPopup extends Popup {

    private static final int DURATION = 3000;

    private Card card;
    private Stage window;

    /**
     * Generates and displays the popup with card information.
     * @Author Dominik Horn
     */
    public void generatePopup() {
        this.setX(getWindowCenterX()-150);
        this.setY(getWindowCenterY()-150);
        this.getContent().addAll(new Rectangle(150, 150, convertColor()));
        Label label = new Label(card.getTitle());
        this.getContent().add(label);
        this.show(this.window);
        // Close the popup after 3 seconds
        this.closePopup();

    }

    /**
     * Calculates the center X coordinate of the window.
     *
     * @return the center X coordinate of the window
     * @Author Dominik Horn
     */
    private Double getWindowCenterX(){
        return (this.window.getWidth()/2) + this.window.getX();
    }

    /**
     * Calculates the center Y coordinate of the window.
     *
     * @return the center Y coordinate of the window
     * @Author Dominik Horn
     */
    private Double getWindowCenterY(){
        return (this.window.getHeight()/3) + this.window.getY();
    }

    /**
     * Closes the popup after a specified duration.
     * @Author Dominik Horn
     */
    private void closePopup(){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(DURATION),
                ae -> this.hide()
        ));
        timeline.play();
    }

    /**
     * Converts the card's color to a JavaFX Color object.
     *
     * @return the converted Color object
     * @Author Dominik Horn
     */
    private Color convertColor(){
        double r = this.card.getColor().getR() / 255.0;
        double g = this.card.getColor().getG() / 255.0;
        double b = this.card.getColor().getB() / 255.0;
        return new Color(r, g, b, 1.0);
    }
}
