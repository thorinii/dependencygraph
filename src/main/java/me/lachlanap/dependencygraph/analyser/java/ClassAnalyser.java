package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.AnalysisBuilder;
import me.lachlanap.dependencygraph.analyser.CouplingStrength;
import me.lachlanap.dependencygraph.analyser.Dependency;
import me.lachlanap.dependencygraph.analyser.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lachlan Phillips
 */
public class ClassAnalyser {

    public AnalysisBuilder analyse(ClassFile classFile) {
        Entity entity = new Entity(classFile.getName());
        List<Dependency> dependencies = new ArrayList<>();

        dependencies.add(pub(entity, classFile.getParent()));

        dependencies.addAll(pub(entity, classFile.getInterfaces()));

        classFile.getConstructors().forEach(c -> {
            dependencies.addAll(pub(entity, c.getArgumentTypes()));
            dependencies.addAll(impl(entity, c.getCode().getReferencedTypes()));
        });

        classFile.getMethods().forEach(m -> {
            dependencies.addAll(pub(entity, m.getArgumentTypes()));
            dependencies.addAll(pub(entity, m.getExceptions()));
            dependencies.addAll(impl(entity, m.getCode().getReferencedTypes()));
            m.getReturnType().ifPresent(t -> dependencies.add(pub(entity, t)));
        });

        classFile.getFields().forEach(f -> dependencies.add(pub(entity, f.getType())));

        if (classFile.isPrivate()) {
            List<Dependency> tmp = dependencies.stream()
                    .map(d -> d.toStrength(CouplingStrength.Implementation))
                    .collect(Collectors.toList());

            dependencies.clear();
            dependencies.addAll(tmp);
        }

        return AnalysisBuilder.single(entity, dependencies);
    }

    private List<Dependency> pub(Entity from, Collection<String> c) {
        return c.stream().map(d -> pub(from, d)).collect(Collectors.toList());
    }

    private List<Dependency> impl(Entity from, Collection<String> c) {
        return c.stream().map(d -> impl(from, d)).collect(Collectors.toList());
    }

    private Dependency pub(Entity from, String name) {
        return new Dependency(from, new Entity(name), CouplingStrength.Public);
    }

    private Dependency impl(Entity from, String name) {
        return new Dependency(from, new Entity(name), CouplingStrength.Implementation);
    }
}
