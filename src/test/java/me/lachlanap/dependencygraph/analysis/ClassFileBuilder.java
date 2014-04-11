package me.lachlanap.dependencygraph.analysis;

import java.util.ArrayList;
import java.util.List;
import me.lachlanap.dependencygraph.ClassFile;
import me.lachlanap.dependencygraph.ClassFile.ConstructorTypes;


public class ClassFileBuilder {

    private final String name;
    private String parent = "";
    private final List<ClassFile.ConstructorTypes> constructors = new ArrayList<>();

    public ClassFileBuilder(String name) {
        this.name = name;
    }

    public ClassFileBuilder(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public ClassFileBuilder setParent(String parent) {
        this.parent = parent;
        return this;
    }

    public ClassFileBuilder appendConstructor(List<String> constructor) {
        constructors.add(new ConstructorTypes(constructor));
        return this;
    }

    public ClassFile build() {
        return new ClassFile(name, parent, constructors);
    }

}
