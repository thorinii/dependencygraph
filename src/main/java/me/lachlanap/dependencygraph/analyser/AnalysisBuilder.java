package me.lachlanap.dependencygraph.analyser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A temporary accumulator for analysis results.
 */
public class AnalysisBuilder {
    public static AnalysisBuilder empty() {
        return new AnalysisBuilder(Collections.emptyList(), Collections.emptyList(), null);
    }

    public static AnalysisBuilder single(Entity entity, List<Dependency> dependencies) {
        return new AnalysisBuilder(Collections.singletonList(entity), dependencies, null);
    }

    private List<Entity> projectEntities;
    private List<Dependency> dependencies;
    private String commonPrefix;

    private AnalysisBuilder(List<Entity> projectEntities, List<Dependency> dependencies, String commonPrefix) {
        this.projectEntities = projectEntities;
        this.dependencies = dependencies;
        this.commonPrefix = commonPrefix;
    }

    public AnalysisBuilder merge(AnalysisBuilder b) {
        return new AnalysisBuilder(mergeList(projectEntities, b.projectEntities),
                                   mergeList(dependencies, b.dependencies),
                                   commonPrefix);
    }

    private <T> List<T> mergeList(List<T> a, List<T> b) {
        List<T> l = new ArrayList<>(a.size() + b.size());
        l.addAll(a);
        l.addAll(b);
        return l;
    }

    private void normalise() {
        projectEntities = projectEntities.stream()
                .collect(Collectors.groupingBy(Entity::getName))
                .entrySet().stream()
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toList());

        Map<String, List<Entity>> canonical = projectEntities.stream().collect(Collectors.groupingBy(Entity::getName));


        Map<Dependency, List<Dependency>> duplicatesDependencies = dependencies.stream().collect(Collectors.groupingBy(a -> a));
        dependencies = duplicatesDependencies.values().stream()
                .map(d -> d.stream().reduce(Dependency::strongest).get())
                .map(d -> normaliseDependency(d, canonical))
                .filter(Dependency::isNotSelf)
                .collect(Collectors.toList());
    }

    private Dependency normaliseDependency(Dependency in, Map<String, List<Entity>> canonical) {
        Entity from = in.getFrom();
        Entity to = in.getTo();

        from = canonical.getOrDefault(from.getName(), Collections.singletonList(from)).get(0);
        to = canonical.getOrDefault(to.getName(), Collections.singletonList(to)).get(0);

        return new Dependency(from, to, in.getStrength());
    }

    public Analysis build() {
        normalise();
        return new Analysis(projectEntities, dependencies, commonPrefix);
    }
}
