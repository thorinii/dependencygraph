package me.lachlanap.dependencygraph.analyser;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A list of entities (various types) and their dependencies.
 */
public class Analysis {
    private final List<Entity> entities;
    private final List<Dependency> dependencies;
    private final String commonPrefix;

    public Analysis(List<Entity> entities, List<Dependency> dependencies) {
        this(entities, dependencies, null);
    }

    public Analysis(List<Entity> entities, List<Dependency> dependencies, String commonPrefix) {
        this.entities = entities;
        this.dependencies = dependencies;
        this.commonPrefix = commonPrefix == null ? findCommonPrefix(entities) : commonPrefix;
    }

    private String findCommonPrefix(List<Entity> entities) {
        if (entities.isEmpty()) return "";

        return entities.stream().collect(Collectors.reducing(
                entities.get(0).getName(),
                Entity::getName,
                this::commonPrefix));
    }

    private String commonPrefix(String a, String b) {
        int i;
        for (i = 0; i < a.length() && i < b.length(); i++)
            if (a.charAt(i) != b.charAt(i))
                break;

        return a.substring(0, i);
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public String getCommonPrefix() {
        return commonPrefix;
    }


    /**
     * Does not filter project entities.
     */
    public Analysis filterDependenciesByTarget(Predicate<String> keepByName) {
        return normalised(entities,
                          dependencies.stream()
                                  .filter(d -> keepByName.test(d.getTo().getName()))
                                  .collect(Collectors.toList()),
                          commonPrefix);
    }

    public Analysis filterEntitiesByName(Predicate<String> keepByName) {
        return filterEntities(e -> keepByName.test(e.getName()));
    }

    public Analysis filterEntities(Predicate<Entity> keepByName) {
        return normalised(entities.stream()
                                  .filter(keepByName)
                                  .collect(Collectors.toList()),
                          dependencies.stream()
                                  .filter(d -> keepByName.test(d.getFrom()) || keepByName.test(d.getTo()))
                                  .collect(Collectors.toList()),
                          commonPrefix);
    }

    public Analysis removeNonProjectDependencies() {
        return normalised(entities,
                          dependencies.stream()
                                  .filter(d -> entities.contains(d.getTo()))
                                  .collect(Collectors.toList()),
                          commonPrefix);
    }

    public Analysis filterRoots() {
        Set<Entity> roots = new HashSet<>(entities);
        for (Dependency d : dependencies)
            roots.remove(d.getTo());

        return normalised(new ArrayList<>(roots), dependencies, commonPrefix);
    }

    public Analysis rewrite(Rewriter map) {
        List<Entity> rewrittenProjectEntities = entities.stream()
                .map(map::apply)
                .collect(Collectors.toList());

        List<Dependency> rewrittenDependencies = dependencies.stream()
                .map(d -> new Dependency(map.apply(d.getFrom()), map.apply(d.getTo()), d.getStrength()))
                .collect(Collectors.toList());

        return normalised(rewrittenProjectEntities, rewrittenDependencies, commonPrefix);
    }

    public Analysis useParent() {
        List<Entity> rewrittenProjectEntities = entities.stream()
                .map(Entity::getParent)
                .collect(Collectors.toList());

        List<Dependency> rewrittenDependencies = dependencies.stream()
                .map(d -> {
                    if (d.getTo().hasParent())
                        return new Dependency(d.getFrom().getParent(), d.getTo().getParent(), d.getStrength());
                    else
                        return new Dependency(d.getFrom().getParent(), d.getTo(), d.getStrength());
                })
                .collect(Collectors.toList());

        return normalised(rewrittenProjectEntities, rewrittenDependencies, null);
    }

    private Analysis normalised(List<Entity> entities, List<Dependency> dependencies, String commonPrefix) {
        entities = entities.stream()
                .collect(Collectors.groupingBy(Entity::getName))
                .entrySet().stream()
                .map(e -> e.getValue().get(0))
                .collect(Collectors.toList());

        Map<String, List<Entity>> canonical = entities.stream().collect(Collectors.groupingBy(Entity::getName));


        Map<Dependency, List<Dependency>> duplicatesDependencies = dependencies.stream().collect(Collectors.groupingBy(a -> a));
        dependencies = duplicatesDependencies.values().stream()
                .map(d -> d.stream().reduce(Dependency::strongest).get())
                .map(d -> normaliseDependency(d, canonical))
                .filter(Dependency::isNotSelf)
                .filter(d -> canonical.containsKey(d.getFrom().getName()))
                .collect(Collectors.toList());

        return new Analysis(entities, dependencies, commonPrefix);
    }

    private Dependency normaliseDependency(Dependency in, Map<String, List<Entity>> canonical) {
        Entity from = in.getFrom();
        Entity to = in.getTo();

        from = canonical.getOrDefault(from.getName(), Collections.singletonList(from)).get(0);
        to = canonical.getOrDefault(to.getName(), Collections.singletonList(to)).get(0);

        return new Dependency(from, to, in.getStrength());
    }


    @Override
    public String toString() {
        return "Analysis{" +
                " entities=\n  " + entities.stream().map(Entity::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                "\n dependencies=\n  " + dependencies.stream().map(Dependency::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                '}';
    }
}
