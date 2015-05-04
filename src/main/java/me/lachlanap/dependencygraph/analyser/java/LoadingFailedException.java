package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.ParsingException;

/**
 *
 * @author Lachlan Phillips
 */
public class LoadingFailedException extends ParsingException {

    public LoadingFailedException(String message) {
        super(message);
    }

    public LoadingFailedException(String message, Throwable cause) {
        super(message, cause);
    }

}
