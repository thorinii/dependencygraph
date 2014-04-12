package me.lachlanap.dependencygraph.analysis.analyser;

import java.util.HashSet;
import java.util.Set;
import me.lachlanap.dependencygraph.analysis.ClassFile;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassAnalyser {

    public ClassAnalysis analyse(ClassFile classFile) {
        Set<String> dependencies = new HashSet<>();

        dependencies.add(classFile.getParent());

        classFile.getConstructors().forEach(c -> {
            dependencies.addAll(c.getArgumentTypes());
            dependencies.addAll(c.getCode().getReferencedTypes());
        });

        classFile.getMethods().forEach(m -> {
            dependencies.addAll(m.getArgumentTypes());
            dependencies.addAll(m.getCode().getReferencedTypes());
            m.getReturnType().ifPresent(t -> dependencies.add(t));
        });

        classFile.getFields().forEach(f -> {
            dependencies.add(f.getType());
        });

        if (dependencies.contains(classFile.getName()))
            dependencies.remove(classFile.getName());

        return new ClassAnalysis(classFile, dependencies);
    }
}
