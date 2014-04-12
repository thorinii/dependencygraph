package me.lachlanap.dependencygraph.analysis.io;

import me.lachlanap.dependencygraph.analysis.io.Loader;
import me.lachlanap.dependencygraph.analysis.io.ResourceLoader;
import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Lachlan Phillips
 */
public class ResourceLoaderTest {

    @Test
    public void loadsCorrectContent() {
        String resource = "UserLostException";
        byte[] expected = loadClassFile(resource);
        Loader loader = new ResourceLoader();

        byte[] actual = loader.load(resource);

        assertThat(actual, is(expected));
    }

}