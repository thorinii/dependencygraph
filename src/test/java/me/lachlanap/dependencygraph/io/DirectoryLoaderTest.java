package me.lachlanap.dependencygraph.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.lachlanap.dependencygraph.Helpers;
import me.lachlanap.dependencygraph.analyser.java.Loader;
import me.lachlanap.dependencygraph.analyser.java.LoaderCouldNotFindClassException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class DirectoryLoaderTest {

    private static final Path source = Paths.get("small-jar.jar");
    private static final Path directory = Paths.get("test-extracted");

    @Test(expected = LoaderCouldNotFindClassException.class)
    public void throwsNotFoundForInvalidClass() {
        Loader loader = new DirectoryLoader(directory);

        try {
            loader.load("NotPresent");
        } finally {
            loader.close();
        }
    }

    @Test
    public void findsClass() {
        Loader loader = new DirectoryLoader(directory);

        loader.load("com.lachlan.jbox2dtests.App");

        loader.close();
    }

    @Test
    public void findsClassesForwards() {
        Loader loader = new DirectoryLoader(directory);

        loader.load("com.lachlan.jbox2dtests.App");
        loader.load("com.lachlan.jbox2dtests.ATest");

        loader.close();
    }

    @Test
    public void findsClassesBackwards() {
        Loader loader = new DirectoryLoader(directory);

        loader.load("com.lachlan.jbox2dtests.ATest");
        loader.load("com.lachlan.jbox2dtests.App");

        loader.close();
    }

    @Test
    public void loadsContentCorrectly() {
        Loader loader = new DirectoryLoader(directory);
        byte[] expected = Helpers.loadClassFile("App");

        byte[] actual = loader.load("com.lachlan.jbox2dtests.App");

        loader.close();

        assertThat(actual, is(expected));
    }

    @BeforeClass
    public static void extractJar() throws IOException {
        Helpers.extractJar(source, directory);
    }

    @AfterClass
    public static void deleteExtracted() throws IOException {
        Helpers.deleteExtracted(directory);
    }

}
