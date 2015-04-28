package me.lachlanap.dependencygraph.analyser;

import java.util.List;

/**
 * A list of entities (various types) and their dependencies.
 */
public class Analysis {
    private final List<Entity> entities;
    private final List<Dependency> dependencies;

    public Analysis(List<Entity> entities, List<Dependency> dependencies) {
        this.entities = entities;
        this.dependencies = dependencies;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "Analysis{" +
                " entities=\n  " + entities.stream().map(Entity::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                "\ndependencies=" + dependencies.stream().map(Dependency::toString).reduce((a, b) -> a + ",\n  " + b).orElse("") +
                '}';
    }
}
