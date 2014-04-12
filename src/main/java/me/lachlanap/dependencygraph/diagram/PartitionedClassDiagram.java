package me.lachlanap.dependencygraph.diagram;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;

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
                    builder.append("label=").append(buildClassName(e.getKey())).append(";\n");

                    e.getValue().stream().forEach(c -> {
                        builder.append(buildLabeledClassName(c)).append(";\n");
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

    private StringBuilder writeDependency(StringBuilder builder, ClassAnalysis analysis, String dependency) {
        return builder.append(buildClassName(analysis.getName()))
                .append(" -> ")
                .append(buildClassName(dependency))
                .append(';').append('\n');
    }

    private String buildLabeledClassName(String name) {
        return buildClassName(name) + " [label=" + buildClassName(name.substring(name.lastIndexOf('.') + 1)) + "]";
    }

    private String buildClassName(String name) {
        return '"' + name + '"';
    }
}
