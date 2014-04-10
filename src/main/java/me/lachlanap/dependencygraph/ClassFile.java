package me.lachlanap.dependencygraph;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassFile {

    private final String name;
    private final String parent;

    public ClassFile(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }

}
