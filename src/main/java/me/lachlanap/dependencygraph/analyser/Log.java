package me.lachlanap.dependencygraph.analyser;

/**
 * A place to log the workings of the algorithm.
 */
public interface Log {
    public void info(String message);
    public void error(String message, Exception error);
}
