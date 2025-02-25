package de.uol.swp.client.card;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.card.Card;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.RequiredArgsConstructor;

/**
 * A class representing a popup for displaying card information.
 */
@RequiredArgsConstructor
public class CardPopup extends Popup {

    private static final int DURATION = 3000;
    private static final double WIDTH = 150.00;
    private static final double HEIGHT = WIDTH * 1.5;
    public static final int ARC_Value = 20;
    public static final Color STROKE_COLOR = Color.BLACK;
    public static final int STROKE_WIDTH = 2;

    private final Card card;
    private final Stage window;
    private final Group group = new Group();

    /**
     * Generates and displays the popup with card information.
     */
    public void generatePopup() {
        this.setX(getWindowCenterX()-WIDTH);
        this.setY(getWindowCenterY()-(HEIGHT / 2));
        this.getContent().add(group);
        generateMainRectangle();
        generateSubRectangle();
        this.show(this.window);
        this.closePopup();

    }

    private void generateMainRectangle() {
        Color backgroundColor = Color.WHITE;
        Rectangle rectangle = generateRectangle(WIDTH, HEIGHT, backgroundColor);

        rectangle.setArcHeight(ARC_Value);
        rectangle.setArcWidth(ARC_Value);
        rectangle.setStroke(STROKE_COLOR);
        rectangle.setStrokeWidth(STROKE_WIDTH);
        this.group.getChildren().add(rectangle);
    }

    private void generateSubRectangle() {
        Color backgroundColor = ColorService.convertColorToJavaFXColor(card.getColor());
        double width = WIDTH * 0.92;
        double height = HEIGHT * 0.92;
        double layoutX = (WIDTH - width) / 2;
        Rectangle rectangle = generateRectangle(width, height, backgroundColor);

        rectangle.setLayoutY((HEIGHT - height)/ 2);

        rectangle.setLayoutX(layoutX);
        this.group.getChildren().add(rectangle);

        Label topLabel = generateLabel(width, height, 0, layoutX);
        Label bottomLabel = generateLabel(width, height,180, layoutX);
        this.group.getChildren().add(topLabel);
        this.group.getChildren().add(bottomLabel);
    }

    private Rectangle generateRectangle(double width, double height, Color color) {
        Rectangle rectangle = new Rectangle(width, height, color);
        rectangle.setArcHeight(ARC_Value);
        rectangle.setArcWidth(ARC_Value);
        rectangle.setStroke(STROKE_COLOR);
        rectangle.setStrokeWidth(STROKE_WIDTH);
        return rectangle;
    }

    private Label generateLabel(double width, double height, double rotation, double layoutX) {
        Label label = new Label(card.getTitle());
        Background background = new Background(new BackgroundFill(Color.BLACK, null, null));
        Font font = Font.font(null, FontWeight.BOLD, 12);

        label.setPrefWidth(width);
        label.setPrefHeight(HEIGHT * 0.1);
        label.setTextFill(Color.WHITE);
        label.setBackground(background);
        label.setFont(font);
        label.setLayoutX(layoutX);
        label.setRotate(rotation);
        if (rotation < 180) {
            label.setLayoutY(group.getLayoutY() + height * 0.06);
        }else {
            label.setLayoutY((group.getLayoutY() + HEIGHT * 0.92) - height * 0.08);
        }


        return label;
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
