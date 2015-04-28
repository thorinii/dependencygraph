package me.lachlanap.dependencygraph.analyser.java.rewrite;

import me.lachlanap.dependencygraph.analyser.java.ClassAnalysis;
import me.lachlanap.dependencygraph.analyser.java.Rewriter;
import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.classAnalysis;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Lachlan Phillips
 */
public class RewriterTest {

    @Test
    public void doesNotChangeWithNullRewriter() {
        Rewriter rewriter = name -> name;
        ClassAnalysis analysis = classAnalysis("test.Class", "test.Dependency");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getName(), is("test.Class"));
        assertThat(result.getDependencies(), hasItem("test.Dependency"));
    }

    @Test
    public void changesWithAnActiveRewriter() {
        Rewriter rewriter = name -> name + "$";
        ClassAnalysis analysis = classAnalysis("test.Class", "test.Dependency");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getName(), is("test.Class$"));
        assertThat(result.getDependencies(), hasItem("test.Dependency$"));
    }

    @Test
    public void removesIntroducedSelfReferences() {
        Rewriter rewriter = name -> name.replace("Dependency", "Main");
        ClassAnalysis analysis = classAnalysis("test.Class$Main", "test.Class$Dependency");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getDependencies().size(), is(0));
    }

}
