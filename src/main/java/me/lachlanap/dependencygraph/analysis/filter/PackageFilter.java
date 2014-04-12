package me.lachlanap.dependencygraph.analysis.filter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageFilter implements Filter {

    private final List<String> packages;

    public PackageFilter(String... packagesToRemove) {
        this.packages = Arrays.asList(packagesToRemove).stream()
                .map(p -> p + ".")
                .collect(Collectors.toList());
    }

    @Override
    public boolean keepPackage(String name) {
        return !packageShouldBeRemoved(name);
    }

    private boolean packageShouldBeRemoved(String name) {
        return packages.stream().anyMatch(p -> name.startsWith(p));
    }
}
