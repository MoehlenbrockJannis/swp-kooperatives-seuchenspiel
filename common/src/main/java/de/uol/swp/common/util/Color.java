package de.uol.swp.common.util;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;

/**
 * Represents a color defined by its RGB components.
 * This class provides methods to create colors using integer values for red, green, and blue components.
 */
@Getter
@EqualsAndHashCode
public class Color implements Serializable {
    private int r;
    private int g;
    private int b;

    /**
     * Default constructor that initializes the color to black (RGB: 0, 0, 0).
     */
    public Color() {
        this.r = 0;
        this.g = 0;
        this.b = 0;
    }

    /**
     * Constructor that initializes the color with specified RGB values.
     * If a parameter is not between 0 and 255, it will be set to 0.
     *
     * @param r The red component (0-255)
     * @param g The green component (0-255)
     * @param b The blue component (0-255)
     */
    public Color(int r, int g, int b) {
        this.r = validateValue(r);
        this.g = validateValue(g);
        this.b = validateValue(b);
    }

    /**
     * Validates that an RGB value is within the range of 0 to 255.
     * If the value is out of this range, it returns 0.
     *
     * @param rgbValue The RGB value to validate
     * @return The validated RGB value
     */
    private int validateValue(int rgbValue) {
        if (rgbValue >= 0 && rgbValue <= 255) {
            return rgbValue;
        } else {
            return 0;
        }
    }

    /**
     * Sets the RGB components of this color.
     * If a parameter is not between 0 and 255, it will be set to 0.
     *
     * @param r The red component (0-255)
     * @param g The green component (0-255)
     * @param b The blue component (0-255)
     */
    public void setColor(int r, int g, int b) {
        this.r = validateValue(r);
        this.g = validateValue(g);
        this.b = validateValue(b);
    }

    @Override
    public String toString() {
        return r + "," + g + "," + b;
    }
}
