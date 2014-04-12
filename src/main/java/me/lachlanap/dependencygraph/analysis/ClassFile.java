package me.lachlanap.dependencygraph.analysis;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassFile {

    private final String name;
    private final String parent;
    private final List<Method> constructors;
    private final List<Method> methods;
    private final List<Field> fields;

    public ClassFile(String name, String parent,
                     List<Method> constructors,
                     List<Method> methods,
                     List<Field> fields) {
        constructors.forEach(c -> {
            if (!c.isConstructor())
                throw new IllegalArgumentException("Constructors must be valid");
        });
        methods.forEach(c -> {
            if (c.isConstructor())
                throw new IllegalArgumentException("Methods cannot be constructors");
        });

        this.name = name;
        this.parent = parent;
        this.constructors = constructors;
        this.methods = methods;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

    public String getPackage() {
        return name.substring(0, name.lastIndexOf('.'));
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

    public List<Field> getFields() {
        return fields;
    }

    public Optional<Field> getField(String name) {
        return fields.stream()
                .filter(f -> f.getName().equals(name))
                .findAny();
    }


    public static class Method {

        private final String name;
        private final List<String> arguments;
        private final Optional<String> returnType;
        private final Code code;

        public Method(String name, List<String> arguments, Code code) {
            this(name, arguments, Optional.empty(), code);
        }

        public Method(String name, List<String> arguments, Optional<String> returnType, Code code) {
            if (name.equals("<init>") && returnType.isPresent())
                throw new IllegalArgumentException("Constructor does not return a type");

            this.name = name;
            this.arguments = arguments;
            this.returnType = returnType;
            this.code = code;
        }

        public boolean isConstructor() {
            return name.equals("<init>");
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

        public Code getCode() {
            return code;
        }

        @Override
        public String toString() {
            return arguments.toString();
        }
    }

    public static class Code {

        private final Set<String> referencedTypes;

        public Code(Set<String> referencedTypes) {
            this.referencedTypes = referencedTypes;
        }

        public Set<String> getReferencedTypes() {
            return referencedTypes;
        }
    }

    public static class Field {

        private final String name;
        private final String type;

        public Field(String name, String type) {
            this.name = name;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }
    }
}
