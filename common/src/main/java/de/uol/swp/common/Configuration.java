package de.uol.swp.common;

/**
 * Provides configuration settings for the application.
 * <p>
 * The {@code Configuration} class contains static configuration values, such as the default port number,
 * and provides utility methods to access these values. This class is not intended to be instantiated.
 * </p>
 */
public class Configuration {

    /**
     * The default port used by the application.
     */
    static final int DEFAULT_PORT = 8899;

    /**
     * Private constructor to prevent instantiation of the class.
     */
    private Configuration() {
    }

    /**
     * Returns the default port number for the application.
     *
     * @return The default port number.
     */
    public static int getDefaultPort() {
        return DEFAULT_PORT;
    }
}