package de.uol.swp.client.util;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TooltipsUtil {

    /**
     * Creates a consistent styled tooltip with specified text.
     *
     * @param text The tooltip text.
     * @return A Tooltip with predefined font, delays, duration, and wrapping.
     */
    public static Tooltip createConsistentTooltip(String text) {
        Tooltip tooltip = new Tooltip(text);

        tooltip.setFont(new Font("System", 18));

        tooltip.setShowDelay(Duration.millis(100));
        tooltip.setShowDuration(Duration.millis(50000));
        tooltip.setHideDelay(Duration.millis(300));

        tooltip.setWrapText(true);

        return tooltip;
    }

    /**
     * Creates a consistent styled tooltip with specified text and maximum width.
     *
     * @param text The tooltip text.
     * @param width The maximum width of the tooltip.
     * @return A Tooltip with predefined font, delays, duration, wrapping, and maximum width.
     */
    public static Tooltip createConsistentTooltip(String text, int width) {
        Tooltip tooltip = createConsistentTooltip(text);
        tooltip.setMaxWidth(width);

        return tooltip;
    }
}
