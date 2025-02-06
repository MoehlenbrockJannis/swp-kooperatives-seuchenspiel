package de.uol.swp.server.env;

/**
 * Interface for reading environment variables
 */
public interface EnvReader {

    /**
     * Reads a string from the environment
     *
     * @param key the key of the environment variable
     * @return the value of the environment variable
     */
    String readString(String key);

    /**
     * Reads an integer from the environment
     *
     * @param key the key of the environment variable
     * @return the value of the environment variable
     */
    int readInt(String key);
}
