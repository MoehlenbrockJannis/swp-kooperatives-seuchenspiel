package de.uol.swp.common.util;

import com.google.common.hash.Hashing;
import lombok.NoArgsConstructor;

import java.nio.charset.StandardCharsets;

/**
 * Utility class for hashing
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class HashUtil {

    /**
     * Calculates the hash for a given String
     *
     * @implSpec the hash method used is sha256
     * @param toHash the String to calculate the hash for
     * @return String containing the calculated hash
     */
    public static String hash(String toHash){
        return Hashing.sha256()
                .hashString(toHash, StandardCharsets.UTF_8)
                .toString();
    }
}
