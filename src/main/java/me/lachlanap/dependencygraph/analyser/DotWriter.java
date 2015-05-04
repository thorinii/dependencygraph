package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Writes the analysis to a Graphviz dot file.
 */
public class DotWriter implements AnalysisWriter {
    private final boolean stripPrefix;

    public DotWriter(boolean stripPrefix) {
        this.stripPrefix = stripPrefix;
    }

    @Override
    public void write(Analysis analysis, PrintWriter out) throws IOException {
        out.println("digraph {");
        out.println("  rankdir=LR;");

        for (Entity entity : analysis.getEntities()) {
            out.println("  \"" + name(entity, analysis) + "\";");
        }

        out.println();

        for (Dependency d : analysis.getDependencies()) {
            Entity from = d.getFrom();
            Entity to = d.getTo();

            out.println("  \"" + name(from, analysis) + "\" -> \"" + name(to, analysis) + "\"" +
                                "[weight=\"" + (d.getStrength() == CouplingStrength.Public ? "2" : "1") + "\"];");
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
