package me.lachlanap.dependencygraph.diagram;

/**
 *
 * @author Lachlan Phillips
 */
public class DiagramWritingException extends RuntimeException {

    public DiagramWritingException(String message) {
        super(message);
    }

    public DiagramWritingException(String message, Throwable cause) {
        super(message, cause);
    }

}
