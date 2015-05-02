package me.lachlanap.dependencygraph.analyser;

/**
 * A unit of code (eg class or package) that has dependencies on something else.
 * It can have a parent.
 */
public final class Entity {
    private final String name;
    private final Entity parent;

    public Entity(String name, Entity parent) {
        this.name = name;
        this.parent = parent;
    }

    public Entity(String name) {
        this.name = name;
        this.parent = null;
    }

    public String getName() {
        return name;
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Entity getParent() {
        if (parent == null)
            throw new UnsupportedOperationException(this + " does not have a parent");
        return parent;
    }

    public Entity changeName(String name) {
        return new Entity(name, this.parent);
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
