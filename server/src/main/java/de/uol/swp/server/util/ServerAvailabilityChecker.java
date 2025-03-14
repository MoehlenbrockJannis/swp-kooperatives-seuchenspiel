package de.uol.swp.server.util;

import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Utility class for checking the availability of a server.
 * <p>
 * The {@code ServerAvailabilityChecker} provides a method to determine whether a server
 * at a specified host and port is reachable within a given timeout.
 * This class is not instantiable and only provides static utility methods.
 * </p>
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ServerAvailabilityChecker {
    /**
     * Checks if a server is available
     *
     * @param host      The server name or IP to connect to
     * @param port      The server port to connect to
     * @param timeoutMs The timeout in milliseconds
     * @return true if the server is available, false otherwise
     */
    public static boolean isServerAvailable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException uhe) {
            return false;
        }
    }
}