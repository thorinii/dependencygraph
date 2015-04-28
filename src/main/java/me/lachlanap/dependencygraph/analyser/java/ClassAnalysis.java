package me.lachlanap.dependencygraph.analyser.java;

import java.util.Set;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassAnalysis {

    private final ClassFile classFile;
    private final Set<String> dependencies;

    public ClassAnalysis(ClassFile classFile, Set<String> dependencies) {
        this.classFile = classFile;
        this.dependencies = dependencies;
    }

    public String getName() {
        return classFile.getName();
    }

    public String getParent() {
        return classFile.getParent();
    }

    public ClassFile getClassFile() {
        return classFile;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Analysis[" + getName()
               + " (" + getParent() + ")"
               + " -> " + dependencies
               + "]";
    }
}
