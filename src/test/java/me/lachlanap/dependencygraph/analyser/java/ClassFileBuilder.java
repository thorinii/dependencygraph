package me.lachlanap.dependencygraph.analyser.java;

import java.util.*;


public class ClassFileBuilder {

    private final String name;
    private String parent = "test.Parent";
    private final List<ClassFile.Method> constructors = new ArrayList<>();
    private final List<ClassFile.Method> methods = new ArrayList<>();
    private final List<ClassFile.Field> fields = new ArrayList<>();

    public ClassFileBuilder(String name) {
        this.name = name;
    }

    public ClassFileBuilder setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public ClassFileBuilder appendConstructor(List<String> arguments) {
        constructors.add(new ClassFile.Method("<init>", arguments, nullCode(), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendConstructorWithCode(List<String> codeReferences) {
        return appendConstructorWithCode(new HashSet<>(codeReferences));
    }

    public ClassFileBuilder appendConstructorWithCode(Set<String> codeReferences) {
        constructors.add(new ClassFile.Method("<init>",
                                    Collections.emptyList(),
                                    new ClassFile.Code(codeReferences), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendMethod(String name) {
        methods.add(new ClassFile.Method(name,
                               Collections.emptyList(),
                               Optional.empty(),
                               nullCode(), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendMethod(String name, List<String> arguments) {
        methods.add(new ClassFile.Method(name,
                               arguments,
                               Optional.empty(),
                               nullCode(), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendMethod(String name, String returnValue, List<String> arguments) {
        methods.add(new ClassFile.Method(name,
                               arguments,
                               Optional.of(returnValue),
                               nullCode(), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendMethodWithCode(String name, List<String> codeReferences) {
        return appendMethodWithCode(name, new HashSet<>(codeReferences));
    }

    public ClassFileBuilder appendMethodWithCode(String name, Set<String> codeReferences) {
        methods.add(new ClassFile.Method(name,
                               Collections.emptyList(),
                               Optional.empty(),
                               new ClassFile.Code(codeReferences), Arrays.asList()));
        return this;
    }

    public ClassFileBuilder appendField(String name, String type) {
        fields.add(new ClassFile.Field(name, type));
        return this;
    }

    private ClassFile.Code nullCode() {
        return new ClassFile.Code(Collections.emptySet());
    }

    public ClassFile build() {
        return new ClassFile(name, parent, Collections.emptyList(), constructors, methods, fields, false);
    }

}
