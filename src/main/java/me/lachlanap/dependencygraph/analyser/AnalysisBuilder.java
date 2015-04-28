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
        return new AnalysisBuilder(Collections.emptyList(), Collections.emptyList());
    }

    public static AnalysisBuilder single(Entity entity, List<Dependency> dependencies) {
        return new AnalysisBuilder(Collections.singletonList(entity), dependencies);
    }

    private final List<Entity> projectEntities;
    private final List<Dependency> dependencies;

    public AnalysisBuilder(Analysis a) {
        this.projectEntities = a.getEntities();
        this.dependencies = a.getDependencies();
    }

    private AnalysisBuilder(List<Entity> projectEntities, List<Dependency> dependencies) {
        this.projectEntities = projectEntities;
        this.dependencies = dependencies;
    }

    public AnalysisBuilder merge(AnalysisBuilder b) {
        return new AnalysisBuilder(mergeList(projectEntities, b.projectEntities), mergeList(dependencies, b.dependencies));
    }

    private <T> List<T> mergeList(List<T> a, List<T> b) {
        List<T> l = new ArrayList<>(a.size() + b.size());
        l.addAll(a);
        l.addAll(b);
        return l;
    }

    public AnalysisBuilder removeDependencies(String startsWith) {
        return new AnalysisBuilder(projectEntities,
                                   dependencies.stream()
                                           .filter(d -> !d.getTo().getName().startsWith(startsWith))
                                           .collect(Collectors.toList()));
    }

    public Analysis build() {
        Map<Dependency, List<Dependency>> duplicatesDependencies = dependencies.stream().collect(Collectors.groupingBy(a -> a));
        List<Dependency> dependencies = duplicatesDependencies.values().stream()
                .map(a -> a.stream().reduce(Dependency::strongest).get())
                .filter(Dependency::isNotSelf)
                .collect(Collectors.toList());

        return new Analysis(projectEntities, dependencies);
    }
}
