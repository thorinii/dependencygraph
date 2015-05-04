package me.lachlanap.dependencygraph.analyser;

/**
 *
 * @author Lachlan Phillips
 */
public class SpiderException extends ParsingException {

    public SpiderException(String message) {
        super(message);
    }

    public SpiderException(String message, Throwable cause) {
        super(message, cause);
    }

}
