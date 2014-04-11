package me.lachlanap.dependencygraph.analysis;

import java.util.HashSet;
import java.util.Set;
import me.lachlanap.dependencygraph.ClassFile;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassAnalyser {

    public ClassAnalysis analyse(ClassFile classFile) {
        Set<String> dependencies = new HashSet<>();

        dependencies.add(classFile.getParent());

        classFile.getConstructors().forEach(c -> dependencies.addAll(c.getArgumentTypes()));

        classFile.getMethods().forEach(c -> {
            dependencies.addAll(c.getArgumentTypes());
            c.getReturnType().ifPresent(t -> dependencies.add(t));
        });

        return new ClassAnalysis(classFile.getName(),
                                 classFile.getParent(),
                                 dependencies);
    }
}
