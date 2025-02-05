package de.uol.swp.server.env;

public interface EnvReader {

    String readString(String key);

    int readInt(String key);
}
