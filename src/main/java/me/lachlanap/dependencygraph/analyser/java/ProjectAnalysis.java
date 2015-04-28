package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.Analysis;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lachlan Phillips
 */
public class ProjectAnalysis {

    private final String rootPackage;
    private final List<ClassAnalysis> classesAnalysis;
    private final List<PackageAnalysis> packagesAnalysis;

    public ProjectAnalysis(Analysis raw) {
        this("", Collections.emptyList(), Collections.emptyList());
    }

    public ProjectAnalysis(String rootPackage,
                           List<ClassAnalysis> classesAnalysis,
                           List<PackageAnalysis> packagesAnalysis) {
        this.rootPackage = rootPackage;
        this.classesAnalysis = classesAnalysis;
        this.packagesAnalysis = packagesAnalysis;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public List<ClassAnalysis> getClassAnalysis() {
        return classesAnalysis;
    }

    public List<PackageAnalysis> getPackageAnalysis() {
        return packagesAnalysis;
    }

    public ProjectAnalysis keepOnlyProjectClasses() {
        return keepOnly(new IncludingPackageFilter(rootPackage));
    }

    public ProjectAnalysis keepOnly(Filter filter) {
        List<ClassAnalysis> filteredClasses = classesAnalysis.stream()
                .filter(c -> filter.keepClass(c.getName()))
                .map(c -> filterClassDependencies(filter, c))
                .collect(Collectors.toList());

        List<PackageAnalysis> filteredPackages = packagesAnalysis.stream()
                .filter(p -> filter.keepPackage(p.getName()))
                .map(p -> filterPackageDependencies(filter, p))
                .collect(Collectors.toList());

        return new ProjectAnalysis(rootPackage, filteredClasses, filteredPackages);
    }

    public ProjectAnalysis keepOnlyAnalysisMatching(Filter filter) {
        List<ClassAnalysis> filteredClasses = classesAnalysis.stream()
                .filter(c -> filter.keepClass(c.getName()))
                .collect(Collectors.toList());

        List<PackageAnalysis> filteredPackages = packagesAnalysis.stream()
                .filter(p -> filter.keepPackage(p.getName()))
                .collect(Collectors.toList());

        return new ProjectAnalysis(rootPackage, filteredClasses, filteredPackages);
    }

    private ClassAnalysis filterClassDependencies(Filter filter, ClassAnalysis in) {
        return new ClassAnalysis(in.getClassFile(),
                                 in.getDependencies().stream()
                .filter(filter::keepClass)
                .collect(Collectors.toSet()));
    }

    private PackageAnalysis filterPackageDependencies(Filter filter, PackageAnalysis in) {
        return new PackageAnalysis(in.getName(),
                                   in.getDependencies().stream()
                .filter(filter::keepPackage)
                .collect(Collectors.toSet()));
    }
}
