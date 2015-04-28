package me.lachlanap.dependencygraph.analyser.java;

/**
 *
 * @author Lachlan Phillips
 */
public interface Filter {

    public default boolean keepClass(String name) {
        if (!name.contains(".")) {
            System.out.println("No package: "+name);
            return keepPackage(name);
        }

        return keepPackage(name.substring(0, name.lastIndexOf('.')));
    }

    public boolean keepPackage(String name);

    public default Filter invert() {
        Filter normal = this;
        return name -> !normal.keepClass(name);
    }
}
