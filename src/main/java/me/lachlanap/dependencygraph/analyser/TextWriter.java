package me.lachlanap.dependencygraph.analyser;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Writes the analysis out as a plain-text sorted list of <code>a -> b</code>.
 */
public class TextWriter implements AnalysisWriter {
    @Override
    public void write(Analysis analysis, PrintWriter out) throws IOException {
        List<Dependency> sortedDependencies =
                analysis.getDependencies().stream()
                        .sorted((a, b) -> {
                            int cmp = a.getFrom().getName().compareTo(b.getFrom().getName());
                            if (cmp == 0) {
                                return a.getTo().getName().compareTo(b.getTo().getName());
                            } else
                                return cmp;
                        })
                        .collect(Collectors.toList());
        for (Dependency d : sortedDependencies) {
            Entity from = d.getFrom();
            Entity to = d.getTo();

            out.println(from.getName() + " -> " + to.getName());
        }
    }
}
