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
        ExcludingPackageFilter filter = new ExcludingPackageFilter("java");

        assertThat(filter.keepClass("java.lang.String"), is(false));
    }

    @Test
    public void removesMatchingNestedPackage() {
        ExcludingPackageFilter filter = new ExcludingPackageFilter("java.lang");

        assertThat(filter.keepClass("java.lang.String"), is(false));
    }

    @Test
    public void keepsNotMatching() {
        ExcludingPackageFilter filter = new ExcludingPackageFilter("java");

        assertThat(filter.keepClass("com.apache.Class"), is(true));
    }

    @Test
    public void keepsNotMatchingWithSimilarName() {
        ExcludingPackageFilter filter = new ExcludingPackageFilter("java");

        assertThat(filter.keepClass("javax.swing.JFrame"), is(true));
    }
}
