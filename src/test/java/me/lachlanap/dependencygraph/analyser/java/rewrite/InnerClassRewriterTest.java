package me.lachlanap.dependencygraph.analyser.java.rewrite;

import me.lachlanap.dependencygraph.analyser.java.ClassAnalysis;
import me.lachlanap.dependencygraph.analyser.java.Rewriter;
import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.classAnalysis;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class InnerClassRewriterTest {

    @Test
    public void leavesNormalClassUntouched() {
        Rewriter rewriter = new InnerClassRewriter();
        ClassAnalysis analysis = classAnalysis("test.Class", "test.Dependency");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getName(), is("test.Class"));
        assertThat(result.getDependencies(), hasItem("test.Dependency"));
    }

    @Test
    public void changesInnerClasses() {
        Rewriter rewriter = new InnerClassRewriter();
        ClassAnalysis analysis = classAnalysis("test.Class$Inner", "test.Dependency$Sub");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getName(), is("test.Class"));
        assertThat(result.getDependencies(), hasItem("test.Dependency"));
    }

    @Test
    public void changesNestedInnerClasses() {
        Rewriter rewriter = new InnerClassRewriter();
        ClassAnalysis analysis = classAnalysis("test.Class$Inner$Nested", "test.Dependency$1$2$3$4");

        ClassAnalysis result = rewriter.rewrite(analysis);

        assertThat(result.getName(), is("test.Class"));
        assertThat(result.getDependencies(), hasItem("test.Dependency"));
    }
}
