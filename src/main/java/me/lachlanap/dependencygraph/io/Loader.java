package me.lachlanap.dependencygraph.io;

/**
 *
 * @author Lachlan Phillips
 */
public interface Loader {

    public byte[] load(String path) throws LoadingFailedException;

    public default void close() {
    }
}
