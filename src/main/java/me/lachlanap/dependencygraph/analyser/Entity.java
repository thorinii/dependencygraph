package me.lachlanap.dependencygraph.analyser;

import java.util.HashSet;
import java.util.Set;

/**
 * A unit of code (eg class or package) that has dependencies on something else.
 * It can have a parent.
 */
public final class Entity {
    private final String name;
    private final Set<Entity> dependencies;

    public Entity(String name) {
        this.name = name;
        this.dependencies = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;
        return name.equals(entity.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
