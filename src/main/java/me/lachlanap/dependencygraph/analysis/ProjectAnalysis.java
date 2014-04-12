package me.lachlanap.dependencygraph.analysis;

import java.util.List;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalysis;
import me.lachlanap.dependencygraph.analysis.filter.Filter;

/**
 *
 * @author Lachlan Phillips
 */
public class ProjectAnalysis {

    private final List<ClassAnalysis> classesAnalysis;
    private final List<PackageAnalysis> packagesAnalysis;

    public ProjectAnalysis(List<ClassAnalysis> classesAnalysis,
                           List<PackageAnalysis> packagesAnalysis) {
        this.classesAnalysis = classesAnalysis;
        this.packagesAnalysis = packagesAnalysis;
    }

    public List<ClassAnalysis> getClassAnalysis() {
        return classesAnalysis;
    }

    public List<PackageAnalysis> getPackageAnalysis() {
        return packagesAnalysis;
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

        return new ProjectAnalysis(filteredClasses, filteredPackages);
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
