package me.curlpipesh.engine.util;

/**
 * @author audrey
 * @since 11/24/15.
 */
public class NoSuchTileException extends IllegalArgumentException {
    public NoSuchTileException() {
        super();
    }

    public NoSuchTileException(final String message) {
        super(message);
    }
}
