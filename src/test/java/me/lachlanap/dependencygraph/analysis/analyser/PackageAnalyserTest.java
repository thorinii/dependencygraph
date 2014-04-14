package me.lachlanap.dependencygraph.analysis.analyser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

import static me.lachlanap.dependencygraph.Helpers.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageAnalyserTest {

    @Test
    public void noClassesNoPackages() {
        PackageAnalyser analyser = new PackageAnalyser();

        List<PackageAnalysis> analysis = analyser.analyse(Collections.emptyList());

        assertThat(analysis.size(), is(0));
    }

    @Test
    public void oneClassOnePackage() {
        PackageAnalyser analyser = new PackageAnalyser();
        ClassAnalysis aClass = emptyClassAnalysis("test.Class");

        List<PackageAnalysis> analysis = analyser.analyse(Arrays.asList(aClass));

        assertThat(analysis.size(), is(1));
        assertThat(analysis.get(0).getName(), is("test"));
    }

    @Test
    public void twoClassesTwoPackage() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<ClassAnalysis> classes = Arrays.asList(
                emptyClassAnalysis("test.package1.Class"),
                emptyClassAnalysis("test.package2.Class"));

        List<PackageAnalysis> analysis = analyser.analyse(classes);

        assertThat(analysis, hasItems(that(p -> p.getName(), is("test.package1")),
                                      that(p -> p.getName(), is("test.package2"))));
    }

    @Test
    public void twoClassesSamePackage() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<ClassAnalysis> classes = Arrays.asList(
                emptyClassAnalysis("test.package.Class1"),
                emptyClassAnalysis("test.package.Class2"));

        List<PackageAnalysis> analysis = analyser.analyse(classes);

        assertThat(analysis.size(), is(1));
        assertThat(analysis, hasItems(that(p -> p.getName(), is("test.package"))));
    }

    @Test
    public void classDependencyToPackageDependency() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<ClassAnalysis> classes = Arrays.asList(
                classAnalysis("test.package1.Class1", "test.package2.Dep1"));

        List<PackageAnalysis> analysis = analyser.analyse(classes);

        assertThat(analysis, hasItems(that(p -> p.getDependencies(), hasItem("test.package2"))));
    }

    @Test
    public void mergesDependencies() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<ClassAnalysis> classes = Arrays.asList(
                classAnalysis("test.main.Class1", "test.dep1.Class", "test.dep2.Class"),
                classAnalysis("test.main.Class2", "test.dep2.Class"));

        List<PackageAnalysis> analysis = analyser.analyse(classes);

        assertThat(analysis, hasItems(that(p -> p.getDependencies(), hasOnly("test.dep1", "test.dep2"))));
    }

    @Test
    public void hasNoSelfDependencies() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<ClassAnalysis> classes = Arrays.asList(
                classAnalysis("test.main.Class1", "test.main.Class2"),
                classAnalysis("test.main.Class2"));

        List<PackageAnalysis> analysis = analyser.analyse(classes);

        assertThat(analysis, hasItems(that(p -> p.getDependencies(), hasOnly())));
    }

    @Test
    public void rootPackageForOnePackageIsItself() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<PackageAnalysis> packages = Arrays.asList(
                packageAnalysis("test.main"));

        String root = analyser.findRootPackageFor(packages).get();

        assertThat(root, is("test.main"));
    }

    @Test
    public void rootPackageForTwoSiblingPackagesIsTheirParent() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<PackageAnalysis> packages = Arrays.asList(
                packageAnalysis("test.main.sub1"),
                packageAnalysis("test.main.sub2"));

        String root = analyser.findRootPackageFor(packages).get();

        assertThat(root, is("test.main"));
    }

    @Test
    public void rootPackageForTwoPackagesIsTheirIntersection() {
        PackageAnalyser analyser = new PackageAnalyser();
        List<PackageAnalysis> packages = Arrays.asList(
                packageAnalysis("test.main"),
                packageAnalysis("test.deeply.nested.sub.package"));

        String root = analyser.findRootPackageFor(packages).get();

        assertThat(root, is("test"));
    }
}
