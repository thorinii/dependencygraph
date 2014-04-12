package me.lachlanap.dependencygraph.analysis.analyser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageAnalyser {

    public List<PackageAnalysis> analyse(List<ClassAnalysis> classes) {
        return classes.stream()
                .collect(Collectors.groupingBy(c -> c.getClassFile().getPackage(),
                                               Collectors.mapping(c -> classesToPackages(c.getDependencies()),
                                                                  Collectors.reducing(new HashSet<String>(),
                                                                                      (e, set) -> {
                                                                                          Set<String> newSet = new HashSet<>(set);
                                                                                          newSet.addAll(e);
                                                                                          return newSet;
                                                                                      }))))
                .entrySet().stream()
                .map(entry -> makePackageAnalysis(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private PackageAnalysis makePackageAnalysis(String name, Set<String> dependencies) {
        return new PackageAnalysis(name, filterSelfDependency(name, dependencies));
    }

    private Set<String> classesToPackages(Set<String> classes) {
        return classes.stream()
                .map(this::classToPackage)
                .collect(Collectors.toSet());
    }

    private String classToPackage(String c) {
        try {
            return c.substring(0, c.lastIndexOf('.'));
        } catch (StringIndexOutOfBoundsException sioobe) {
            throw new RuntimeException("'" + c + "' does not have a package", sioobe);
        }
    }

    private Set<String> filterSelfDependency(String name, Set<String> dependencies) {
        return dependencies.stream()
                .filter(p -> !p.equals(name))
                .collect(Collectors.toSet());
    }
}
