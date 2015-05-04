package me.lachlanap.dependencygraph.analyser;

/**
 * Created by lachlan on 04/05/15.
 */
public class ParsingException extends RuntimeException {
    public ParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParsingException(String message) {
        super(message);
    }
}
