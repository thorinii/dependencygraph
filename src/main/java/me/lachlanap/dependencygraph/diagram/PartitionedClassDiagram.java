package me.lachlanap.dependencygraph.diagram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analyser.java.ProjectAnalysis;
import me.lachlanap.dependencygraph.analyser.java.ClassAnalysis;

/**
 *
 * @author Lachlan Phillips
 */
public class PartitionedClassDiagram implements Diagram {

    private final List<ClassAnalysis> analysis;

    public PartitionedClassDiagram(ProjectAnalysis analysis) {
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

        writePartitions(builder);

        writeDependencyList(builder);

        builder.append("}");

        return builder.toString();
    }

    private StringBuilder writePartitions(StringBuilder builder) {
        Set<String> classes = new HashSet<>();

        classes.addAll(analysis.stream().map(c -> c.getName()).collect(Collectors.toList()));
        classes.addAll(analysis.stream().flatMap(c -> c.getDependencies().stream()).collect(Collectors.toList()));

        classes.stream()
                .collect(Collectors.groupingBy(c -> c.substring(0, c.lastIndexOf("."))))
                .entrySet().forEach(e -> {
                    builder.append("subgraph \"cluster_").append(e.getKey()).append("\" {\n");
                    builder.append("label=");
                    buildClassName(builder, e.getKey());
                    builder.append(";\n");

                    e.getValue().stream().forEach(c -> {
                        buildLabeledClassName(builder, c);
                        builder.append(";\n");
                    });

                    builder.append("}\n");
                });
        return builder;
    }

    private StringBuilder writeDependencyList(StringBuilder builder) {
        analysis.forEach(a -> {
            a.getDependencies().forEach(d -> {
                writeDependency(builder, a, d);
            });
        });
        return builder;
    }

    private void writeDependency(StringBuilder builder, ClassAnalysis analysis, String dependency) {
        buildClassName(builder, analysis.getName());
        builder.append(" -> ");
        buildClassName(builder, dependency);
        builder.append(";\n");
    }

    private void buildLabeledClassName(StringBuilder builder, String name) {
        buildClassName(builder, name);
        builder.append(" [label=");
        buildClassName(builder, name.substring(name.lastIndexOf('.') + 1));
        builder.append(']');
    }

    private void buildClassName(StringBuilder builder, String name) {
        builder.append('"').append(name).append('"');
    }
}
