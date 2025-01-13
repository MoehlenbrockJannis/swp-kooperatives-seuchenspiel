package de.uol.swp.client.plague;

import de.uol.swp.client.util.ColorService;
import de.uol.swp.common.map.Field;
import de.uol.swp.common.plague.Plague;
import de.uol.swp.common.plague.PlagueCube;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Creates all {@link PlagueCube} for a given {@link Field}
 */
public class PlagueCubeMarker extends Group {

    private final double width;
    private final double height;
    private final double depth;
    private final double foreignCubeWidth;
    private final double foreignCubeHeight;
    private final double foreignCubeDepth;
    private final double cubeSpacing;
    @Getter
    @Setter
    private int numberOfAssociatedPlagueCubes;
    private final Text associatedPlagueCubeCounter;

    /**
     * Constructor
     * <p>
     * Initializes the {@link PlagueCubeMarker} with a width of 20
     * </p>
     */
    public PlagueCubeMarker(Field field) {
        this(field, 20);
    }

    /**
     * Constructor
     * <p>
     * Initializes the {@link PlagueCubeMarker} with the given width
     * </p>
     *
     * @param width width of the PlagueCubeMarker
     */
    public PlagueCubeMarker(Field field, double width) {
        List<PlagueCube> plagueCubesOfAssociatedPlague = field.getPlagueCubesOfPlague(field.getPlague());
        this.numberOfAssociatedPlagueCubes = plagueCubesOfAssociatedPlague.size();

        this.width = width;
        this.height = width * 0.6;
        this.depth = width * 0.5;

        double foreignCubeSizeFactor = 0.75;
        this.foreignCubeWidth = width * foreignCubeSizeFactor;
        this.foreignCubeHeight = height * foreignCubeSizeFactor;
        this.foreignCubeDepth = depth * foreignCubeSizeFactor;

        this.cubeSpacing = width * 0.08;

        associatedPlagueCubeCounter = new Text();

        initPlagueCubes(field);

        this.setMouseTransparent(true);
    }

    /**
     * Creates {@link PlagueCubeIcon}s on initialization
     *
     * @param field the {@link Field} that is represented by the {@link PlagueCubeMarker}
     */
    private void initPlagueCubes(Field field) {
        initAssociatedPlagueCubes(field);

        addForeignPlagueCubes(field);

        createPlagueCubeCounter();
    }

    /**
     * Creates the associated {@link PlagueCubeIcon}s on initialization
     *
     * @param field the {@link Field} that is represented by the {@link PlagueCubeMarker}
     */
    private void initAssociatedPlagueCubes(Field field) {
        Color associatedPlagueColor = ColorService.convertColorToJavaFXColor(field.getPlague().getColor());

        if (numberOfAssociatedPlagueCubes == 1) {
            createSinglePlagueCube(associatedPlagueColor);
        } else if (numberOfAssociatedPlagueCubes == 2) {
            createPairOfPlagueCubes(associatedPlagueColor);
        } else if (numberOfAssociatedPlagueCubes >= 3) {
            createPyramidOfPlagueCubes(associatedPlagueColor);
        }
    }

    /**
     * Creates a single {@link PlagueCubeIcon} and add it to the {@link PlagueCubeMarker}
     *
     * @param color the color of the {@link PlagueCubeIcon}
     */
    public void createSinglePlagueCube(Color color) {
        this.getChildren().add(new PlagueCubeIcon(width, color, false));
    }

    /**
     * Creates a pair of {@link PlagueCubeIcon}s and adds them to the {@link PlagueCubeMarker}
     *
     * @param color the color of the {@link PlagueCubeIcon}s
     */
    public void createPairOfPlagueCubes(Color color) {
        PlagueCubeIcon plagueCube1 = new PlagueCubeIcon(width, color, false);
        PlagueCubeIcon plagueCube2 = new PlagueCubeIcon(width, color, false);

        plagueCube1.setLayoutX(plagueCube1.getWidth() / 2 + cubeSpacing);
        plagueCube2.setLayoutX(-plagueCube2.getWidth() / 2 - cubeSpacing);

        this.getChildren().addAll(plagueCube1, plagueCube2);
    }

    /**
     * Creates a pyramid of {@link PlagueCubeIcon}s and adds them to the {@link PlagueCubeMarker}
     *
     * @param color the color of the {@link PlagueCubeIcon}s
     */
    public void createPyramidOfPlagueCubes(Color color) {
        PlagueCubeIcon plagueCube1 = new PlagueCubeIcon(width, color, false);
        PlagueCubeIcon plagueCube2 = new PlagueCubeIcon(width, color, false);
        PlagueCubeIcon plagueCube3 = new PlagueCubeIcon(width, color, false);

        plagueCube1.setLayoutX(plagueCube1.getWidth() / 2 + cubeSpacing);
        plagueCube2.setLayoutX(-plagueCube2.getWidth() / 2 - cubeSpacing);

        plagueCube2.setLayoutY(plagueCube2.getTotalHeight() / 2 - cubeSpacing / 2);
        plagueCube1.setLayoutY(plagueCube1.getTotalHeight() / 2 - cubeSpacing / 2);
        plagueCube3.setLayoutY(-plagueCube3.getTotalHeight() / 2 + cubeSpacing / 2);

        this.getChildren().addAll(plagueCube1, plagueCube2, plagueCube3);
    }

    /**
     * Creates and adds {@link PlagueCubeIcon}s of all foreign {@link Plague}s to the {@link PlagueCubeMarker}
     *
     * @param field the {@link Field} for which foreign plagueCubes are created
     */
    public void addForeignPlagueCubes(Field field) {
        Map<Plague, Integer> foreignPlagueCubeCounts = field.getPlagueCubeAmounts();
        foreignPlagueCubeCounts.remove(field.getPlague());

        int numberOfForeignPlagueTypes = field.getNumberOfForeignPlagueCubeTypes();
        int currentPlagueTypeCount = 0;

        for (Map.Entry<Plague, Integer> entry : foreignPlagueCubeCounts.entrySet()) {
            if (entry.getValue() > 0) {
                addSingleForeignPlagueCube(numberOfForeignPlagueTypes, currentPlagueTypeCount, entry.getKey().getColor(), entry.getValue());

                currentPlagueTypeCount++;
            }
        }
    }

    /**
     * Creates and adds a single foreign {@link PlagueCubeIcon} to the {@link PlagueCubeMarker}
     *
     * @param foreignPlagueTypes     total amount of foreign {@link Plague}s types
     * @param currentPlagueTypeCount index of the foreign {@link Plague} type for which a {@link PlagueCubeIcon} is being created
     * @param plagueColor            color of the foreign {@link Plague}
     * @param numberOfPlagueCubes    amount of {@link PlagueCube}s of the foreign {@link Plague}
     */
    private void addSingleForeignPlagueCube(int foreignPlagueTypes, int currentPlagueTypeCount, de.uol.swp.common.util.Color plagueColor, int numberOfPlagueCubes) {
        double yOffset = calculateYOffsetForForeignPlagueCubes(foreignPlagueTypes, currentPlagueTypeCount);

        Color color = ColorService.convertColorToJavaFXColor(plagueColor);
        PlagueCubeIcon plagueCube = new PlagueCubeIcon(foreignCubeWidth, color, true, numberOfPlagueCubes);

        plagueCube.setLayoutX(calculateXOffsetForForeignPlagueCubes());
        plagueCube.setLayoutY(yOffset);

        this.getChildren().add(plagueCube);
    }

    /**
     * Calculates the x offset for the foreign plagueCubes
     *
     * @return calculated x offset for the given plague type
     */
    private double calculateXOffsetForForeignPlagueCubes() {
        double xOffset = width / 2 + cubeSpacing * 2 + foreignCubeWidth / 2;

        if (numberOfAssociatedPlagueCubes >= 2) {
            xOffset += width / 2 + cubeSpacing;
        }

        return xOffset;
    }

    /**
     * Calculates the y offset for the foreign plagueCubes
     *
     * @param foreignPlagueTypes amount of foreign plague types
     * @param currentPlagueCount out of all plague types the number of the current plague type to be displayed
     * @return calculated y offset for the given plague type
     */
    private double calculateYOffsetForForeignPlagueCubes(int foreignPlagueTypes, int currentPlagueCount) {
        double stackedCubesHeight = foreignPlagueTypes * (foreignCubeHeight + foreignCubeDepth) + (foreignPlagueTypes - 1) * cubeSpacing;
        return -stackedCubesHeight / 2 + foreignCubeDepth + currentPlagueCount * (foreignCubeHeight + foreignCubeDepth + cubeSpacing);
    }

    /**
     * Deletes all associated (not foreign) {@link PlagueCubeIcon}s found in the {@link PlagueCubeMarker}
     */
    public void deleteAllAssociatedPlagueCubes() {
        this.getChildren().removeIf(node ->
                node instanceof PlagueCubeIcon plagueCube && !plagueCube.isForeignPlagueCube()
        );
    }

    /**
     * Deletes all foreign {@link PlagueCubeIcon}s found in the {@link PlagueCubeMarker}
     */
    public void deleteAllForeignPlagueCubes() {
        this.getChildren().removeIf(node ->
                node instanceof PlagueCubeIcon plagueCube && plagueCube.isForeignPlagueCube()
        );
    }

    /**
     * Creates the {@link #associatedPlagueCubeCounter} used to display the amount of associated plagueCubes ({@link #numberOfAssociatedPlagueCubes})
     */
    private void createPlagueCubeCounter() {
        associatedPlagueCubeCounter.setFont(Font.font(null, FontWeight.BOLD, ((height + depth) * 2 + cubeSpacing * 2) * 0.75));
        associatedPlagueCubeCounter.setFill(Color.WHITE);
        associatedPlagueCubeCounter.setStroke(Color.BLACK);
        associatedPlagueCubeCounter.setStrokeType(StrokeType.OUTSIDE);

        updatePlagueCubeCounter();

        if (numberOfAssociatedPlagueCubes <= 3) {
            setAssociatedPlagueCubeCounterVisibility(false);
        }

        this.getChildren().add(associatedPlagueCubeCounter);
    }

    /**
     * Updates the text and position of the {@link #associatedPlagueCubeCounter}
     */
    public void updatePlagueCubeCounter() {
        associatedPlagueCubeCounter.setText(String.valueOf(numberOfAssociatedPlagueCubes));

        double textWidth = associatedPlagueCubeCounter.getLayoutBounds().getWidth();
        double textHeight = associatedPlagueCubeCounter.getLayoutBounds().getHeight();

        double xPosition = -textWidth / 2;
        double yPosition = textHeight / 2;

        associatedPlagueCubeCounter.setX(xPosition);
        associatedPlagueCubeCounter.setY(yPosition - (textHeight / 4.3));

        associatedPlagueCubeCounter.toFront();
    }

    /**
     * Sets the visibility of the {@link #associatedPlagueCubeCounter}
     * {@code true} will make the counter visible, {@code false} will make it invisible
     *
     * @param isVisible whether the counter should be set visible or not
     */
    public void setAssociatedPlagueCubeCounterVisibility(boolean isVisible) {
        associatedPlagueCubeCounter.setVisible(isVisible);
    }
}
