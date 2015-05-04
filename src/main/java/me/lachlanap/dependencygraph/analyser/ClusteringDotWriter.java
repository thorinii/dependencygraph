package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Writes the analysis to a Graphviz dot file.
 */
public class ClusteringDotWriter implements AnalysisWriter {
    private final Analysis raw;
    private final boolean stripPrefix;

    public ClusteringDotWriter(Analysis raw, boolean stripPrefix) {
        this.raw = raw;
        this.stripPrefix = stripPrefix;
    }

    @Override
    public void write(Analysis analysis, PrintWriter out) throws IOException {
        Set<Entity> group = new HashSet<>(analysis.getEntities());
        Entity parent = analysis.getEntities().get(0).getParent();

        Analysis a = raw.filterEntities(group::contains);

        Set<Entity> others = new HashSet<>();
        others.addAll(a.getDependencies().stream().map(Dependency::getFrom).collect(Collectors.toSet()));
        others.addAll(a.getDependencies().stream().map(Dependency::getTo).collect(Collectors.toSet()));
        others.removeAll(group);


        out.println("digraph {");
        out.println("  rankdir=LR;");

        out.println("  subgraph cluster_group {");
        out.println("    style=filled;");
        out.println("    color=lightgrey;");
        out.println("    node [style=filled,color=white];");
        out.println("    label=\"" + parent.getName() + "\";");
        for (Entity entity : group) {
            out.println("    \"" + name(entity, analysis) + "\";");
        }
        out.println("  }");

        out.println();
        out.println("  node [color=grey];");

        for (Entity entity : others) {
            out.println("  \"" + name(entity, analysis) + "\";");
        }

        out.println();

        for (Dependency d : analysis.getDependencies()) {
            Entity from = d.getFrom();
            Entity to1 = d.getTo();

            boolean isOutside = (others.contains(d.getFrom()) || others.contains(d.getTo()));
            out.println("  \"" + name(from, analysis) + "\" -> \"" + name(to1, analysis) + "\"" +
                                "[weight=" + (isOutside ? "1" : "10") + "," +
                                "color=" + (isOutside ? "grey" : "black") + "];");
        }

        out.println("}");
    }

    private String name(Entity e, Analysis raw) {
        String stripped = stripPrefix ? e.getName(raw.getCommonPrefix()) : e.getName();
        if (stripped.startsWith("."))
            stripped = stripped.substring(1);

        if (stripped.isEmpty())
            return "(root)";
        else
            return stripped;
    }
}
