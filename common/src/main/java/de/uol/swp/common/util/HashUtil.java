package de.uol.swp.common.util;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for hashing
 */
public class HashUtil {

    /**
     * Private constructor to prevent instantiation
     */
    private HashUtil() {
    }

    /**
     * Calculates the hash for a given String
     *
     * @implSpec the hash method used is sha256
     * @param toHash the String to calculate the hash for
     * @return String containing the calculated hash
     * @since 2019-09-04
     */
    public static String hash(String toHash){
        return Hashing.sha256()
                .hashString(toHash, StandardCharsets.UTF_8)
                .toString();
    }
}
