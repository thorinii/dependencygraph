package me.lachlanap.dependencygraph.analysis;

import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Lachlan Phillips
 */
public class ProjectAnalysisTest {

    @Test
    public void keepsThoseNotMatching() {
        ProjectAnalysis analysis = projectAnalysisOfClasses(classAnalysis("keep.Class", "keep.Dep"));

        ProjectAnalysis result = analysis.keepOnly(c -> true);

        assertThat(result.getClassAnalysis(), hasItems(that(c -> c.getName(), is("keep.Class"))));
        assertThat(result.getClassAnalysis(), hasItems(that(c -> c.getDependencies(), hasItem("keep.Dep"))));
    }

    @Test
    public void removesMatchingClasses() {
        ProjectAnalysis analysis = projectAnalysisOfClasses(classAnalysis("remove.Class", "keep.Dep"));

        ProjectAnalysis result = analysis.keepOnly(c -> !c.startsWith("remove"));

        assertThat(result.getClassAnalysis().size(), is(0));
    }

    @Test
    public void removesMatchingDependencyClasses() {
        ProjectAnalysis analysis = projectAnalysisOfClasses(classAnalysis("keep.Class", "remove.Dep"));

        ProjectAnalysis result = analysis.keepOnly(c -> !c.startsWith("remove"));

        assertThat(result.getClassAnalysis(), hasItems(that(c -> c.getDependencies().size(), is(0))));
    }
}
