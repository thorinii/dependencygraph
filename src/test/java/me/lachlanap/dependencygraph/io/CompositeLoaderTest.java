package me.lachlanap.dependencygraph.io;

import java.util.Arrays;
import java.util.Collections;

import me.lachlanap.dependencygraph.analyser.java.Loader;
import me.lachlanap.dependencygraph.analyser.java.LoaderCouldNotFindClassException;
import me.lachlanap.dependencygraph.analyser.java.LoadingFailedException;
import org.junit.Test;

/**
 *
 * @author Lachlan Phillips
 */
public class CompositeLoaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void needsAtLeastOneLoader() {
        new CompositeLoader(Collections.emptyList());
    }

    @Test(expected = LoaderCouldNotFindClassException.class)
    public void throwsCouldNotFindWhenNoLoaderCanFind() {
        Loader loader = new CompositeLoader(Arrays.asList(c -> {
            throw new LoaderCouldNotFindClassException(c);
        }, c -> {
            throw new LoaderCouldNotFindClassException(c);
        }));

        loader.load("Unfindable");
    }

    @Test
    public void findsUsingFirstLoader() {
        Loader loader = new CompositeLoader(Arrays.asList(c -> new byte[1],
                                                          c -> {
                                                              throw new LoadingFailedException("Shouldn't call the second loader");
                                                          }));

        loader.load("Findable");
    }

    @Test
    public void findsUsingSecondLoaderWhenFirstLoaderCannotFind() {
        Loader loader = new CompositeLoader(Arrays.asList(c -> {
            throw new LoaderCouldNotFindClassException(c);
        }, c -> new byte[1]));

        loader.load("Findable");
    }

    @Test(expected = LoadingFailedException.class)
    public void passesThroughLoadingFailedExceptions() {
        Loader loader = new CompositeLoader(Arrays.asList(c -> {
            throw new LoadingFailedException("Successfully passed through the loading error");
        }));

        loader.load("ErrorCausing");
    }
}
