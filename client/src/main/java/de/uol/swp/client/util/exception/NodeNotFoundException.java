package de.uol.swp.client.util.exception;

/**
 * Exception thrown when a {@link javafx.scene.Node} could not be found.
 */
public class NodeNotFoundException extends Exception {
    public NodeNotFoundException(String message) {
        super(message);
    }
}
