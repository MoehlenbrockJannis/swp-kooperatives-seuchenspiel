package de.uol.swp.client.util;

import javafx.scene.paint.Color;

/**
 * Utility class for color conversion between {@link de.uol.swp.common.util.Color} objects and JavaFX Color ({@link Color}) objects.
 * This class provides static methods to convert color representations for use in JavaFX applications.
 *
 * @author David Scheffler
 * @since 2024-09-23
 */
public class ColorService {

    /**
     * Private constructor to prevent instantiation
     *
     * @author David Scheffler
     * @since 2024-09-23
     */
    private ColorService() {
    }

    /**
     * Converts an object of {@link de.uol.swp.common.util.Color} to a JavaFX Color ({@link Color})
     *
     * @param color {@link de.uol.swp.common.util.Color} object to be converted to JavaFX Color
     * @return Converted JavaFX Color
     * @see de.uol.swp.common.util.Color
     * @see Color
     * @author David Scheffler
     * @since 2024-09-23
     */
    public static Color convertColorToJavaFXColor(de.uol.swp.common.util.Color color) {
        return Color.rgb(color.getR(), color.getG(), color.getB());
    }

    /**
     * Converts a JavaFX Color ({@link Color}) to an object of {@link de.uol.swp.common.util.Color}
     *
     * @param color Color to be converted to {@link de.uol.swp.common.util.Color} object
     * @return Converted {@link de.uol.swp.common.util.Color} object
     * @see de.uol.swp.common.util.Color
     * @see Color
     * @author David Scheffler
     * @since 2024-09-23
     */
    public static de.uol.swp.common.util.Color convertJavaFXColorToColor(Color color) {
        return new de.uol.swp.common.util.Color(convertRgbDoubleToInt(color.getRed()), convertRgbDoubleToInt(color.getGreen()), convertRgbDoubleToInt(color.getBlue()));
    }

    /**
     * Converts a double RGB value (from 0.0 to 1.0) to an integer RGB value (from 0 to 255).
     *
     * @param d The double value to convert
     * @return The integer representation of the RGB value
     * @author David Scheffler
     * @since 2024-09-23
     */
    private static int convertRgbDoubleToInt(double d) {
        return (int) (d * 255);
    }

}
