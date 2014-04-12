package me.lachlanap.dependencygraph.analysis.analyser;

import java.util.Set;
import me.lachlanap.dependencygraph.analysis.ClassFile;

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
