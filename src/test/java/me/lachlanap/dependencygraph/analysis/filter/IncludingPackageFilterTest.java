package me.lachlanap.dependencygraph.analysis.filter;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class IncludingPackageFilterTest {

    @Test
    public void keepsMatching() {
        IncludingPackageFilter filter = new IncludingPackageFilter("java");

        assertThat(filter.keepClass("java.lang.String"), is(true));
    }

    @Test
    public void removesNotMatching() {
        IncludingPackageFilter filter = new IncludingPackageFilter("java");

        assertThat(filter.keepClass("com.apache.Class"), is(false));
    }

    @Test
    public void keepsMatchingNestedPackage() {
        IncludingPackageFilter filter = new IncludingPackageFilter("java.lang");

        assertThat(filter.keepClass("java.lang.String"), is(true));
    }

    @Test
    public void removesNotMatchingWithSimilarName() {
        IncludingPackageFilter filter = new IncludingPackageFilter("java");

        assertThat(filter.keepClass("javax.swing.JFrame"), is(false));
    }
}
