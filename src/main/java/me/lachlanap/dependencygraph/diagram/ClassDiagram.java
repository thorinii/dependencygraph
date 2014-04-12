package me.lachlanap.dependencygraph.diagram;

import java.util.List;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;

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

    private StringBuilder writeDependency(StringBuilder builder, ClassAnalysis analysis, String dependency) {
        return builder.append(buildClassName(analysis.getName()))
                .append(" -> ")
                .append(buildClassName(dependency))
                .append(';').append('\n');
    }

    private String buildClassName(String name) {
        return '"' + name + '"';
    }
}
