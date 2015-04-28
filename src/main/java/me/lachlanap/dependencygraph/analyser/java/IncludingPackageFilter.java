package me.lachlanap.dependencygraph.analyser.java;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Lachlan Phillips
 */
public class IncludingPackageFilter implements Filter {

    private final List<String> packages;

    public IncludingPackageFilter(String... packages) {
        this.packages = Arrays.asList(packages);
    }

    @Override
    public boolean keepPackage(String name) {
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
