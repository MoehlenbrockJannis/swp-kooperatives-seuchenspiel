package de.uol.swp.client.card;

import de.uol.swp.client.util.ColorService;
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
     */
    public void generatePopup() {
        this.setX(getWindowCenterX()-150);
        this.setY(getWindowCenterY()-150);
        this.getContent().addAll(new Rectangle(150, 150, ColorService.convertColorToJavaFXColor(this.card.getColor())));
        Label label = new Label(card.getTitle());
        label.setTextFill(getContrastingColor(ColorService.convertColorToJavaFXColor(this.card.getColor())));
        this.getContent().add(label);
        this.show(this.window);
        // Close the popup after 3 seconds
        this.closePopup();

    }

    /**
     * Calculates the center X coordinate of the window.
     *
     * @return the center X coordinate of the window
     */
    private Double getWindowCenterX(){
        return (this.window.getWidth()/2) + this.window.getX();
    }

    /**
     * Calculates the center Y coordinate of the window.
     *
     * @return the center Y coordinate of the window
     */
    private Double getWindowCenterY(){
        return (this.window.getHeight()/3) + this.window.getY();
    }

    /**
     * Closes the popup after a specified duration.
     */
    private void closePopup(){
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(DURATION),
                ae -> this.hide()
        ));
        timeline.play();
    }

    private Color getContrastingColor(Color color) {
        double brightness = (color.getRed() * 0.299 + color.getGreen() * 0.587 + color.getBlue() * 0.114);
        return brightness > 0.5 ? Color.BLACK : Color.WHITE;
    }
}
