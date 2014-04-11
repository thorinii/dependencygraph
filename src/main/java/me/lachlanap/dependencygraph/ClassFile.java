package me.lachlanap.dependencygraph;

import java.util.List;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassFile {

    private final String name;
    private final String parent;
    private final List<ConstructorTypes> constructors;

    public ClassFile(String name, String parent, List<ConstructorTypes> constructors) {
        this.name = name;
        this.parent = parent;
        this.constructors = constructors;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public List<ConstructorTypes> getConstructors() {
        return constructors;
    }

    public static class ConstructorTypes {

        private final List<String> types;

        public ConstructorTypes(List<String> types) {
            this.types = types;
        }

        public List<String> getTypes() {
            return types;
        }

        @Override
        public String toString() {
            return types.toString();
        }
    }
}
