package me.lachlanap.dependencygraph.analysis.spider;

import java.nio.file.Paths;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class JarSpiderTest {

    @Test
    public void correctlyListsClasses() {
        Spider spider = new JarSpider(Paths.get("small-jar.jar"));

        List<String> classes = spider.findClassesToAnalyse();

        assertThat(classes, hasItems("com.lachlan.jbox2dtests.ATest",
                                     "com.lachlan.jbox2dtests.App"));
    }

}
