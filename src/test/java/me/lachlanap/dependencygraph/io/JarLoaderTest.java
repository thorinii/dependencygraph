package me.lachlanap.dependencygraph.io;

import java.nio.file.Paths;
import me.lachlanap.dependencygraph.Helpers;
import me.lachlanap.dependencygraph.analyser.java.Loader;
import me.lachlanap.dependencygraph.analyser.java.LoaderCouldNotFindClassException;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Lachlan Phillips
 */
public class JarLoaderTest {

    @Test(expected = LoaderCouldNotFindClassException.class)
    public void throwsNotFoundForInvalidClass() {
        Loader loader = new JarLoader(Paths.get("small-jar.jar"));

        try {
            loader.load("NotPresent");
        } finally {
            loader.close();
        }
    }

    @Test
    public void findsClass() {
        Loader loader = new JarLoader(Paths.get("small-jar.jar"));

        loader.load("com.lachlan.jbox2dtests.App");

        loader.close();
    }

    @Test
    public void findsClassesForwards() {
        Loader loader = new JarLoader(Paths.get("small-jar.jar"));

        loader.load("com.lachlan.jbox2dtests.App");
        loader.load("com.lachlan.jbox2dtests.ATest");

        loader.close();
    }

    @Test
    public void findsClassesBackwards() {
        Loader loader = new JarLoader(Paths.get("small-jar.jar"));

        loader.load("com.lachlan.jbox2dtests.ATest");
        loader.load("com.lachlan.jbox2dtests.App");

        loader.close();
    }

    @Test
    public void loadsContentCorrectly() {
        Loader loader = new JarLoader(Paths.get("small-jar.jar"));
        byte[] expected = Helpers.loadClassFile("App");

        byte[] actual = loader.load("com.lachlan.jbox2dtests.App");

        loader.close();

        assertThat(actual, is(expected));
    }
}
