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
    private static final int DURATION = 2000;
    private static final double WIDTH = 150.00;
    private static final double HEIGHT = WIDTH * 1.5;
    public static final double LABEL_HEIGHT = HEIGHT * 0.1;
    public static final int ARC_VALUE = 20;
    public static final Color STROKE_COLOR = Color.BLACK;
    public static final int STROKE_WIDTH = 2;
    public static final int FONT_SIZE = 12;
    public static final double OPACITY = 0.75;
    private static final double SUB_RECTANGLE_SCALE = 0.92;
    private static final Color LABEL_BACKGROUND_COLOR = Color.BLACK;
    private static final Color LABEL_TEXT_COLOR = Color.WHITE;
    public static final double LABEL_Y_OFFSET_FACTOR = 0.06;
    public static final Color BACKGROUND_COLOR_MAIN_RECTANGLE = Color.WHITE;

    private final Card card;
    private final Stage window;
    private final Group group = new Group();

    /**
     * Generates and displays the popup with card information.
     */
    public void generatePopup() {
        this.setX(getWindowCenterX()-WIDTH);
        this.setY(getWindowCenterY());
        this.group.setOpacity(OPACITY);
        this.getContent().add(group);
        generateMainRectangle();
        generateSubRectangle();
        this.show(this.window);
        this.closePopup();
    }

    /**
     * Generates the main rectangle and adds it to the group.
     */
    private void generateMainRectangle() {
        Rectangle rectangle = generateRectangle(WIDTH, HEIGHT, BACKGROUND_COLOR_MAIN_RECTANGLE);

        this.group.getChildren().add(rectangle);
    }

    /**
     * Generates the sub-rectangle and its labels, and adds them to the group.
     */
    private void generateSubRectangle() {
        Color backgroundColor = ColorService.convertColorToJavaFXColor(card.getColor());
        double width = WIDTH * SUB_RECTANGLE_SCALE;
        double height = HEIGHT * SUB_RECTANGLE_SCALE;
        double layoutX = (WIDTH - width) / 2;
        double layoutY = (HEIGHT - height) / 2;
        double labelYOffset = height * LABEL_Y_OFFSET_FACTOR;
        double topLabelLayoutY = layoutY + labelYOffset;
        double bottomLabelLayoutY = (layoutY + height - LABEL_HEIGHT) - labelYOffset;
        Rectangle rectangle = generateRectangle(width, height, backgroundColor);

        rectangle.setLayoutY(layoutY);
        rectangle.setLayoutX(layoutX);
        this.group.getChildren().add(rectangle);

        Label topLabel = generateLabel(width,0, layoutX, topLabelLayoutY);
        Label bottomLabel = generateLabel(width,180, layoutX, bottomLabelLayoutY);
        this.group.getChildren().add(topLabel);
        this.group.getChildren().add(bottomLabel);
    }

    /**
     * Generates a rectangle with the specified properties.
     *
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the fill color of the rectangle
     * @return the generated rectangle
     */
    private Rectangle generateRectangle(double width, double height, Color color) {
        Rectangle rectangle = new Rectangle(width, height, color);
        rectangle.setArcHeight(ARC_VALUE);
        rectangle.setArcWidth(ARC_VALUE);
        rectangle.setStroke(STROKE_COLOR);
        rectangle.setStrokeWidth(STROKE_WIDTH);
        return rectangle;
    }

    /**
     * Generates a label with the specified properties.
     *
     * @param width the width of the label
     * @param rotation the rotation angle of the label
     * @param layoutX the X coordinate for the label's layout
     * @param layoutY the Y coordinate for the label's layout
     * @return the generated label
     */
    private Label generateLabel(double width, double rotation, double layoutX, double layoutY) {
        Label label = new Label(card.getTitle());
        Background background = new Background(new BackgroundFill(LABEL_BACKGROUND_COLOR, null, null));
        Font font = Font.font(null, FontWeight.BOLD, FONT_SIZE);

        label.setPrefWidth(width);
        label.setPrefHeight(LABEL_HEIGHT);
        label.setTextFill(LABEL_TEXT_COLOR);
        label.setBackground(background);
        label.setFont(font);
        label.setLayoutX(layoutX);
        label.setLayoutY(layoutY);
        label.setRotate(rotation);

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
}
