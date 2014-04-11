package me.lachlanap.dependencygraph.io;

/**
 *
 * @author Lachlan Phillips
 */
public class LoadingFailedException extends RuntimeException {

    public LoadingFailedException(String message) {
        super(message);
    }

    public LoadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
