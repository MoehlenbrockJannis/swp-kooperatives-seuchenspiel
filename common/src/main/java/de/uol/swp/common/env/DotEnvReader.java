package de.uol.swp.common.env;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * A utility class for reading environment variables using {@link Dotenv}.
 * <p>
 * The {@code DotEnvReader} provides methods to read string and integer values
 * from an environment file located in the specified directory.
 * </p>
 */
public class DotEnvReader implements EnvReader {
    private static final String PATH_TO_ENV = "./";

    private final Dotenv dotenv = Dotenv.configure()
            .directory(PATH_TO_ENV)
            .load();

    @Override
    public String readString(String key) {
        return dotenv.get(key);
    }

    @Override
    public int readInt(String key) {
        return Integer.parseInt(dotenv.get(key));
    }
}
