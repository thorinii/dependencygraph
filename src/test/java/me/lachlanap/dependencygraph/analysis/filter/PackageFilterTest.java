package me.lachlanap.dependencygraph.analysis.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageFilterTest {

    @Test
    public void removesMatching() {
        PackageFilter filter = new PackageFilter("java");

        assertThat(filter.keepClass("java.lang.String"), is(false));
    }

    @Test
    public void keepsNotMatching() {
        PackageFilter filter = new PackageFilter("java");

        assertThat(filter.keepClass("com.apache.Class"), is(true));
    }

    @Test
    public void keepsNotMatchingWithSimilarName() {
        PackageFilter filter = new PackageFilter("java");

        assertThat(filter.keepClass("javax.swing.JFrame"), is(true));
    }
}
