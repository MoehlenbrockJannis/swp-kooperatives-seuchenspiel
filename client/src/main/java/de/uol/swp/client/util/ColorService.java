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

    /**
     * Adjusts the brightness of the given {@link Color} by changing each rgb value by the specified percentage.
     * A positive value will lighten the color, and a negative value will darken it.
     *
     * @param color The color to adjust
     * @param percentage The percentage as decimal number to adjust the brightness by
     * @return The adjusted color
     */
    public static Color adjustBrightness(Color color, double percentage) {
        double factor = 1 + percentage;

        double newRed = Math.min(Math.max(color.getRed() * factor, 0), 1);
        double newGreen = Math.min(Math.max(color.getGreen() * factor, 0), 1);
        double newBlue = Math.min(Math.max(color.getBlue() * factor, 0), 1);

        return new Color(newRed, newGreen, newBlue, color.getOpacity());
    }

    /**
     * Interpolates between two colors based on a progress value.
     *
     * @param start the starting color
     * @param end the ending color
     * @param progress the progress value between 0.0 and 1.0
     * @return the interpolated color
     *
     * @throws IllegalArgumentException if progress is outside the range [0.0, 1.0]
     */
    public static Color interpolateColor(Color start, Color end, double progress) {
        if (progress < 0.0 || progress > 1.0) {
            throw new IllegalArgumentException("Progress must be between 0.0 and 1.0");
        }
        double r = start.getRed() + (end.getRed() - start.getRed()) * progress;
        double g = start.getGreen() + (end.getGreen() - start.getGreen()) * progress;
        double b = start.getBlue() + (end.getBlue() - start.getBlue()) * progress;
        return new Color(r, g, b, 1.0);
    }

}
