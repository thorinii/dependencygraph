package me.lachlanap.dependencygraph.analyser.java;

import java.util.*;
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

    public Optional<String> findRootPackageFor(List<PackageAnalysis> packagesAnalysis) {
        return packagesAnalysis.stream()
                .map((PackageAnalysis p) -> p.getName())
                .map((String s) -> s.split("\\."))
                .reduce(this::intersectionOf)
                .map((String[] p) -> Arrays.stream(p).collect(Collectors.joining(".")));
    }

    private String[] intersectionOf(String[] p1, String[] p2) {
        List<String> similarParts = new ArrayList<>();

        for (int i = 0; i < p1.length && i < p2.length; i++) {
            if (p1[i].equals(p2[i]))
                similarParts.add(p1[i]);
            else
                return similarParts.toArray(new String[similarParts.size()]);
        }

        return similarParts.toArray(new String[similarParts.size()]);
    }
}
