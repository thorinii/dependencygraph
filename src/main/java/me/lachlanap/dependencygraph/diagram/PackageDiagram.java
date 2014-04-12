package me.lachlanap.dependencygraph.diagram;

import java.util.List;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalysis;

/**
 *
 * @author Lachlan Phillips
 */
public class PackageDiagram implements Diagram {


    private final List<PackageAnalysis> analysis;

    public PackageDiagram(ProjectAnalysis analysis) {
        this.analysis = analysis.getPackageAnalysis();
    }

    @Override
    public String toString() {
        return buildDiagram();
    }

    @Override
    public String buildDiagram() {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");

        writeDependencyList(builder);

        builder.append("}");

        return builder.toString();
    }

    private StringBuilder writeDependencyList(StringBuilder builder) {
        analysis.forEach(a -> {
            a.getDependencies().forEach(d -> {
                writeDependency(builder, a, d);
            });
        });
        return builder;
    }

    private StringBuilder writeDependency(StringBuilder builder, PackageAnalysis analysis, String dependency) {
        return builder.append(buildPackageName(analysis.getName()))
                .append(" -> ")
                .append(buildPackageName(dependency))
                .append(';').append('\n');
    }

    private String buildPackageName(String name) {
        return '"' + name + '"';
    }
}
