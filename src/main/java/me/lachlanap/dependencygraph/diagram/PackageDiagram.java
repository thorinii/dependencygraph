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

    private void writeDependency(StringBuilder builder, PackageAnalysis analysis, String dependency) {
        buildPackageName(builder, analysis.getName());
        builder.append(" -> ");
        buildPackageName(builder, dependency);
        builder.append(";\n");
    }

    private void buildPackageName(StringBuilder builder, String name) {
        builder.append('"').append(name).append('"');
    }
}
