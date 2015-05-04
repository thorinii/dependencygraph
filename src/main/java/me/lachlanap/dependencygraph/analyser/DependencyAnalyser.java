package me.lachlanap.dependencygraph.analyser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @author Lachlan Phillips
 */
public class DependencyAnalyser {
    private final Spider spider;
    private final EntityAnalyser entityAnalyser;
    private final Rewriter rewriter;

    public DependencyAnalyser(Spider spider, EntityAnalyser entityAnalyser, Rewriter rewriter) {
        this.spider = spider;
        this.entityAnalyser = entityAnalyser;
        this.rewriter = rewriter;
    }

    public void analyseAndReport(Log log, Path root, UnaryOperator<Analysis> filter) throws IOException {
        log.info("Analysing");
        Analysis raw = analyse();

        log.info("Filtering");
        raw = filter.apply(raw);

        AnalysisOutput output = AnalysisOutput.createClean(root, raw);

        log.info("Dumping raw analysis");
        output.write("raw.json", new JsonWriter());
        output.write("raw.txt", new TextWriter());

        log.info("Generating diagrams");
        writeDiagrams(output, "all", false);
        writeDiagrams(output.filterAnalysis(Analysis::removeNonProjectDependencies), "proj", true);
        output.filterAnalysis(Analysis::filterRoots).write("roots.txt", new TextWriter());

        log.info("Done");
    }

    private Analysis analyse() {
        Analysis entities =
                spider.findClassesToAnalyse().parallelStream()
                        .map(entityAnalyser::analyse)
                        .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge).build();

        return entities.rewrite(rewriter);
    }

    private void writeDiagrams(AnalysisOutput output, String prefix, boolean stripPrefix) throws IOException {
        output.write(prefix + "-classes.dot", new DotWriter(stripPrefix));
        //writePerParentInOut(out, prefix, raw, true, stripPrefix);

        output.filterAnalysis(Analysis::useParent)
                .write(prefix + "-packages.dot", new DotWriter(stripPrefix));
    }

    private void writePerParentInOut(Path to, String prefix, Analysis raw, boolean impl, boolean stripPrefix) throws IOException {
        Map<Entity, Set<Entity>> groups = raw.getEntities().stream().collect(Collectors.groupingBy(Entity::getParent, Collectors.toSet()));

        for (Map.Entry<Entity, Set<Entity>> e : groups.entrySet()) {
            Entity parent = e.getKey();
            Set<Entity> group = e.getValue();

            Analysis a = raw.filterEntities(group::contains);

            Set<Entity> others = new HashSet<>();
            others.addAll(a.getDependencies().stream().map(Dependency::getFrom).collect(Collectors.toSet()));
            others.addAll(a.getDependencies().stream().map(Dependency::getTo).collect(Collectors.toSet()));
            others.removeAll(group);


            try (BufferedWriter bw = Files.newBufferedWriter(
                    to.resolve(prefix + "-classes-" + parent.getName() + ".dot"),
                    StandardCharsets.UTF_8);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println("digraph {");
                out.println("  rankdir=LR;");

                out.println("  subgraph cluster_group {");
                out.println("    style=filled;");
                out.println("    color=lightgrey;");
                out.println("    node [style=filled,color=white];");
                out.println("    label=\"" + parent.getName() + "\";");
                for (Entity entity : group) {
                    out.println("    \"" + name(entity, stripPrefix, a) + "\";");
                }
                out.println("  }");

                out.println();
                out.println("  node [color=grey];");

                for (Entity entity : others) {
                    out.println("  \"" + name(entity, stripPrefix, a) + "\";");
                }

                out.println();

                for (Dependency d : a.getDependencies()) {
                    Entity from = d.getFrom();
                    Entity to1 = d.getTo();

                    if (d.getStrength() == CouplingStrength.Implementation && !impl)
                        continue;

                    boolean isOutside = (others.contains(d.getFrom()) || others.contains(d.getTo()));
                    out.println("  \"" + name(from, stripPrefix, a) + "\" -> \"" + name(to1, stripPrefix, a) + "\"" +
                                        "[weight=" + (isOutside ? "1" : "10") + "," +
                                        "color=" + (isOutside ? "grey" : "black") + "];");
                }

                out.println("}");
            }
        }
    }

    private String name(Entity e, boolean stripPrefix, Analysis raw) {
        String stripped = stripPrefix ? e.getName(raw.getCommonPrefix()) : e.getName();
        if (stripped.startsWith("."))
            stripped = stripped.substring(1);

        if (stripped.isEmpty())
            return "(root)";
        else
            return stripped;
    }
}
