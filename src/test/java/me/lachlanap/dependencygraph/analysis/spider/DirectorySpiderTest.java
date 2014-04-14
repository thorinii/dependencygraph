package me.lachlanap.dependencygraph.analysis.spider;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import me.lachlanap.dependencygraph.Helpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class DirectorySpiderTest {

    private final Path source = Paths.get("small-jar.jar");
    private final Path directory = Paths.get("test-extracted");

    @Test
    public void correctlyListsClasses() {
        Spider spider = new DirectorySpider(directory);

        List<String> classes = spider.findClassesToAnalyse();

        assertThat(classes, hasItems("com.lachlan.jbox2dtests.ATest",
                                     "com.lachlan.jbox2dtests.App"));
    }

    @Before
    public void extractJar() throws IOException {
        Helpers.extractJar(source, directory);
    }

    @After
    public void deleteExtracted() throws IOException {
        Helpers.deleteExtracted(directory);
    }
}
