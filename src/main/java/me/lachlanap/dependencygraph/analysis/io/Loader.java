package me.lachlanap.dependencygraph.analysis.io;

/**
 *
 * @author Lachlan Phillips
 */
public interface Loader {

    public byte[] load(String path) throws LoadingFailedException;

    public default void close() {
    }
}
