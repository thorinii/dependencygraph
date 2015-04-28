package me.lachlanap.dependencygraph.analyser;

/**
 * A unit of code (eg class or package) that has dependencies on something else.
 * It can have a parent.
 */
public final class Entity {
    private final String name;

    public Entity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return name;
    }
}
