package me.lachlanap.dependencygraph.analyser;

/**
 * A dependency (or coupling) from one entity to another.
 */
public class Dependency {
    private final Entity from, to;
    private final CouplingStrength strength;

    public Dependency(Entity from, Entity to, CouplingStrength strength) {
        this.from = from;
        this.to = to;
        this.strength = strength;
    }

    public Entity getFrom() {
        return from;
    }

    public Entity getTo() {
        return to;
    }

    public CouplingStrength getStrength() {
        return strength;
    }

    public Dependency strongest(Dependency b) {
        return (strength.strongerThan(b.strength)) ? this : b;
    }

    public boolean isNotSelf() {
        return !isSelf();
    }

    public boolean isSelf() {
        return from.equals(to);
    }

    public Dependency toStrength(CouplingStrength strength) {
        return new Dependency(from, to, strength);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dependency that = (Dependency) o;

        if (!from.equals(that.from)) return false;
        if (!to.equals(that.to)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return from + " -> " + to + " (" + strength + ")";
    }

}
