package me.lachlanap.dependencygraph.analyser;

import me.lachlanap.dependencygraph.analyser.java.LoadingFailedException;

/**
 *
 * @author Lachlan Phillips
 */
public interface Loader {

    public byte[] load(String path) throws LoadingFailedException;

    public default void close() {
    }
}
