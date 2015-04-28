package me.lachlanap.dependencygraph.analyser.java;

import java.util.Set;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageAnalysis {

    private final String name;
    private final Set<String> dependencies;

    public PackageAnalysis(String name, Set<String> dependencies) {
        this.name = name;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }
}
