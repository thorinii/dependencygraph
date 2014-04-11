package me.lachlanap.dependencygraph.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.lachlanap.dependencygraph.ClassFile;
import me.lachlanap.dependencygraph.ClassFile.Method;


public class ClassFileBuilder {

    private final String name;
    private String parent = "test.Parent";
    private final List<ClassFile.Method> constructors = new ArrayList<>();
    private final List<ClassFile.Method> methods = new ArrayList<>();

    public ClassFileBuilder(String name) {
        this.name = name;
    }

    public ClassFileBuilder setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public ClassFileBuilder appendConstructor(List<String> arguments) {
        constructors.add(new Method("<init>", arguments));
        return this;
    }

    public ClassFileBuilder appendMethod(String name, String returnValue, List<String> arguments) {
        methods.add(new Method(name, arguments, Optional.of(returnValue)));
        return this;
    }

    public ClassFile build() {
        return new ClassFile(name, parent, constructors, methods);
    }

}
