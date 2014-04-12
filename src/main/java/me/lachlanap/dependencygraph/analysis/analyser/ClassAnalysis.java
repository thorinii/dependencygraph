package me.lachlanap.dependencygraph.analysis.analyser;

import java.util.Set;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassAnalysis {

    private final String name;
    private final String parent;
    private final Set<String> dependencies;

    public ClassAnalysis(String name, String parent, Set<String> dependencies) {
        this.name = name;
        this.parent = parent;
        this.dependencies = dependencies;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Analysis[" + name
               + " (" + parent + ")"
               + " -> " + dependencies
               + "]";
    }
}
