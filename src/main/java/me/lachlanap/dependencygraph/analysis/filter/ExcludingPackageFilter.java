package me.lachlanap.dependencygraph.analysis.filter;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Lachlan Phillips
 */
public class ExcludingPackageFilter implements Filter {

    private final List<String> packages;

    public ExcludingPackageFilter(String... packagesToRemove) {
        this.packages = Arrays.asList(packagesToRemove);
    }

    @Override
    public boolean keepPackage(String name) {
        return !packageShouldBeRemoved(name);
    }

    private boolean packageShouldBeRemoved(String name) {
        return packages.stream().anyMatch(p -> {
            if (name.length() < p.length())
                return false;
            if (name.equals(p))
                return true;

            String whatsLeft = name.substring(p.length());
            return whatsLeft.startsWith(".");
        });
    }
}
