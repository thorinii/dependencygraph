package me.lachlanap.dependencygraph;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassFile {

    private final String name;
    private final String parent;
    private final List<Method> constructors;
    private final List<Method> methods;

    public ClassFile(String name, String parent,
                     List<Method> constructors,
                     List<Method> methods) {
        this.name = name;
        this.parent = parent;
        this.constructors = constructors;
        this.methods = methods;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public List<Method> getConstructors() {
        return constructors;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public Optional<Method> getMethod(String name) {
        return methods.stream()
                .filter(m -> m.getName().equals(name))
                .findAny();
    }

    public static class Method {

        private final String name;
        private final List<String> arguments;
        private final Optional<String> returnType;

        public Method(String name, List<String> arguments) {
            this.name = name;
            this.arguments = arguments;
            this.returnType = Optional.empty();
        }

        public Method(String name, List<String> arguments, Optional<String> returnType) {
            this.name = name;
            this.arguments = arguments;
            this.returnType = returnType;
        }

        public String getName() {
            return name;
        }

        public List<String> getArgumentTypes() {
            return arguments;
        }

        public Optional<String> getReturnType() {
            return returnType;
        }

        @Override
        public String toString() {
            return arguments.toString();
        }
    }
}
