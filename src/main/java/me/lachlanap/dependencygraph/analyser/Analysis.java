package me.lachlanap.dependencygraph.analyser;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A list of entities (various types) and their dependencies.
 */
public class Analysis {
    private final List<Entity> entities;
    private final List<Dependency> dependencies;
    private final String commonPrefix;

    public Analysis(List<Entity> entities, List<Dependency> dependencies) {
        this.entities = entities;
        this.dependencies = dependencies;
        this.commonPrefix = findCommonPrefix(entities);
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

    @Override
    public String toString() {
        return "Analysis{" +
                " entities=\n  " + entities.stream().map(Entity::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                "\ndependencies=" + dependencies.stream().map(Dependency::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                '}';
    }
}
