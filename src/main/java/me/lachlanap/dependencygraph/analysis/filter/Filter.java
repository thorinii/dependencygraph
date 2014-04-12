package me.lachlanap.dependencygraph.analysis.filter;

/**
 *
 * @author Lachlan Phillips
 */
public interface Filter {

    public default boolean keepClass(String name) {
        return keepPackage(name.substring(0, name.lastIndexOf('.')));
    }

    public boolean keepPackage(String name);

    public default Filter invert() {
        Filter normal = this;
        return name -> !normal.keepClass(name);
    }
}
