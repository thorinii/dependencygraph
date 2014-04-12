package me.lachlanap.dependencygraph.analysis;

import java.util.*;
import me.lachlanap.dependencygraph.analysis.ClassFile.Code;
import me.lachlanap.dependencygraph.analysis.ClassFile.Field;
import me.lachlanap.dependencygraph.analysis.ClassFile.Method;


public class ClassFileBuilder {

    private final String name;
    private String parent = "test.Parent";
    private final List<Method> constructors = new ArrayList<>();
    private final List<Method> methods = new ArrayList<>();
    private final List<Field> fields = new ArrayList<>();

    public ClassFileBuilder(String name) {
        this.name = name;
    }

    public ClassFileBuilder setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public ClassFileBuilder appendConstructor(List<String> arguments) {
        constructors.add(new Method("<init>", arguments, nullCode()));
        return this;
    }

    public ClassFileBuilder appendConstructorWithCode(List<String> codeReferences) {
        return appendConstructorWithCode(new HashSet<>(codeReferences));
    }

    public ClassFileBuilder appendConstructorWithCode(Set<String> codeReferences) {
        constructors.add(new Method("<init>",
                                    Collections.emptyList(),
                                    new Code(codeReferences)));
        return this;
    }

    public ClassFileBuilder appendMethod(String name) {
        methods.add(new Method(name,
                               Collections.emptyList(),
                               Optional.empty(),
                               nullCode()));
        return this;
    }

    public ClassFileBuilder appendMethod(String name, List<String> arguments) {
        methods.add(new Method(name,
                               arguments,
                               Optional.empty(),
                               nullCode()));
        return this;
    }

    public ClassFileBuilder appendMethod(String name, String returnValue, List<String> arguments) {
        methods.add(new Method(name,
                               arguments,
                               Optional.of(returnValue),
                               nullCode()));
        return this;
    }

    public ClassFileBuilder appendMethodWithCode(String name, List<String> codeReferences) {
        return appendMethodWithCode(name, new HashSet<>(codeReferences));
    }

    public ClassFileBuilder appendMethodWithCode(String name, Set<String> codeReferences) {
        methods.add(new Method(name,
                               Collections.emptyList(),
                               Optional.empty(),
                               new Code(codeReferences)));
        return this;
    }

    public ClassFileBuilder appendField(String name, String type) {
        fields.add(new Field(name, type));
        return this;
    }

    private Code nullCode() {
        return new Code(Collections.emptySet());
    }

    public ClassFile build() {
        return new ClassFile(name, parent, constructors, methods, fields);
    }

}
