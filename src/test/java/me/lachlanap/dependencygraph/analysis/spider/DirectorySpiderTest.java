package me.lachlanap.dependencygraph.analysis.spider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import me.lachlanap.dependencygraph.Util;
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
        Util.createBlankDirectory(directory);

        try (JarInputStream stream = new JarInputStream(Files.newInputStream(source))) {
            JarEntry entry;

            while ((entry = stream.getNextJarEntry()) != null) {
                String name = (entry.getName().startsWith("/")) ? entry.getName().substring(1) : entry.getName();

                if (entry.isDirectory()) {
                    Files.createDirectories(directory.resolve(name));
                } else {
                    Files.copy(stream, directory.resolve(name));
                }
            }
        }
    }

    @After
    public void deleteExtracted() throws IOException {
        Util.createBlankDirectory(directory);
        Files.delete(directory);
    }
}
