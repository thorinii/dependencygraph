package me.lachlanap.dependencygraph;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Lachlan Phillips
 */
public class DependencyAnalyserConfig {

    public final List<Path> toAnalyse = new ArrayList<>();
    public Path outputPath;
    public Optional<String> rootProjectPackage = Optional.empty();
    private boolean filterCoreJava = true;


    public void addToAnalyse(String whatToAnalyse) {
        toAnalyse.add(Paths.get(whatToAnalyse));
    }

    public void setOutputPath(String out) {
        outputPath = Paths.get(out);
    }

    public void setRootProjectPackage(String root) {
        rootProjectPackage = Optional.of(root);
    }

    public boolean filterCoreJava() {
        return filterCoreJava;
    }

    public void setFilterCoreJava(boolean filterCoreJava) {
        this.filterCoreJava = filterCoreJava;
    }
}
