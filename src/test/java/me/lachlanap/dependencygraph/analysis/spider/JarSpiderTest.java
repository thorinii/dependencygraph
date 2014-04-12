package me.lachlanap.dependencygraph.analysis.spider;

import me.lachlanap.dependencygraph.analysis.spider.JarSpider;
import me.lachlanap.dependencygraph.analysis.spider.Spider;
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
        Spider spider = new JarSpider(getClass().getResource("/small-jar.jar"));

        List<String> classes = spider.findClassesToAnalyse();

        assertThat(classes, hasItems("com.lachlan.jbox2dtests.ATest",
                                     "com.lachlan.jbox2dtests.App"));
    }

}
