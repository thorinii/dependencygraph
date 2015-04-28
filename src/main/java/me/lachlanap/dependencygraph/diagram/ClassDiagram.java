package me.lachlanap.dependencygraph.diagram;

import java.util.List;
import me.lachlanap.dependencygraph.analyser.java.ProjectAnalysis;
import me.lachlanap.dependencygraph.analyser.java.ClassAnalysis;

/**
 *
 * @author Lachlan Phillips
 */
public class ClassDiagram implements Diagram {

    private final List<ClassAnalysis> analysis;

    public ClassDiagram(ProjectAnalysis analysis) {
        this.analysis = analysis.getClassAnalysis();
    }

    @Override
    public String toString() {
        return buildDiagram();
    }

    @Override
    public String buildDiagram() {
        StringBuilder builder = new StringBuilder(4096);
        builder.append("digraph {\n");

        writeDependencyList(builder);

        builder.append("}");

        return builder.toString();
    }

    private void writeDependencyList(StringBuilder builder) {
        analysis.forEach(a -> {
            a.getDependencies().forEach(d -> {
                writeDependency(builder, a, d);
            });
        });
    }

    private void writeDependency(StringBuilder builder, ClassAnalysis analysis, String dependency) {
        buildClassName(builder, analysis.getName());
        builder.append(" -> ");
        buildClassName(builder, dependency);
        builder.append(";\n");
    }

    private void buildClassName(StringBuilder builder, String name) {
        builder.append('"').append(name).append('"');
    }
}
