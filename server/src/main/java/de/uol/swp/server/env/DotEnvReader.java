package de.uol.swp.server.env;

import io.github.cdimascio.dotenv.Dotenv;

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
