package me.lachlanap.dependencygraph.analyser;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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

    public void analyse(Log log, Path out, boolean filterCoreJava) throws IOException {
        log.info("Analysing");
        Analysis raw = analyse();

        log.info("Filtering");
        if (filterCoreJava) { // TODO: refactor this into Java specific
            raw = new AnalysisBuilder(raw).filterDependenciesByTarget(n -> !n.startsWith("java.")).build();
            raw = new AnalysisBuilder(raw).filterEntities(n -> !n.contains("Exception")).build();
        }

        FileUtil.createBlankDirectory(out);

        log.info("Dumping raw analysis");
        dumpRawJson(out.resolve("raw.json"), raw);
        dumpRawText(out.resolve("raw.txt"), raw);

        log.info("Generating diagrams");
        writeDiagrams(out, "all", raw, false);
        writeDiagrams(out, "proj", new AnalysisBuilder(raw).removeNonProjectDependencies().build(), true);

        log.info("Done");
    }

    private Analysis analyse() {
        AnalysisBuilder entities =
                spider.findClassesToAnalyse().parallelStream()
                        .map(entityAnalyser::analyse)
                        .reduce(AnalysisBuilder.empty(), AnalysisBuilder::merge);

        AnalysisBuilder rewritten = entities.rewrite(rewriter);

        return rewritten.build();
    }

    private void writeDiagrams(Path out, String prefix, Analysis raw, boolean stripPrefix) throws IOException {
        writeEntities(out.resolve(prefix + "-classes.dot"), raw, false, stripPrefix);
        writeEntities(out.resolve(prefix + "-classes-impl.dot"), raw, true, stripPrefix);

        writeUnusedEntities(out.resolve(prefix + "-classes-isolated.dot"), raw, stripPrefix);

        Analysis packages = new AnalysisBuilder(raw).useParent().build();
        writeEntities(out.resolve(prefix + "-packages.dot"), packages, false, stripPrefix);
        writeEntities(out.resolve(prefix + "-packages-impl.dot"), packages, true, stripPrefix);
    }

    private void dumpRawJson(Path toFile, Analysis raw) throws IOException {
        Map<String, Integer> entityIds = new HashMap<>();
        int nextId = 0;
        try (BufferedWriter bw = Files.newBufferedWriter(toFile, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("{\"entities\": [");

            for (Entity entity : raw.getEntities()) {
                if (entity.hasParent())
                    out.println("  {\"name\":\"" + entity.getName() + "\"," +
                                        "\"parent\":\"" + entity.getParent().getName() + "\"},");
                else
                    out.println("  {\"name\":\"" + entity.getName() + "\"},");
                entityIds.put(entity.getName(), nextId++);
            }

            out.println(" ], dependencies: [");

            for (Dependency d : raw.getDependencies()) {
                Entity from = d.getFrom();
                int fromId = entityIds.get(from.getName());

                if (entityIds.containsKey(d.getTo().getName())) {
                    int toId = entityIds.get(d.getTo().getName());
                    out.println("  {\"from\":" + fromId + "," +
                                        "\"to\":" + toId +
                                        ",\"strength\":\"" + (d.getStrength() == CouplingStrength.Public ? "pub" : "imp") + "\"},");

                } else {
                    out.println("  {\"from\":" + fromId + "," +
                                        "\"to\":\"" + d.getTo().getName() + "\"" +
                                        ",\"strength\":\"" + (d.getStrength() == CouplingStrength.Public ? "pub" : "imp") + "\"},");
                }
            }

            out.println(" ]\n}");
        }
    }

    private void dumpRawText(Path toFile, Analysis raw) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(toFile, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(bw)) {

            List<Dependency> sortedDependencies =
                    raw.getDependencies().stream()
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

    private void writeEntities(Path toFile, Analysis raw, boolean impl, boolean stripPrefix) throws IOException {
        try (BufferedWriter bw = Files.newBufferedWriter(toFile, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("digraph {");
            out.println("  rankdir=LR;");

            for (Entity entity : raw.getEntities()) {
                out.println("  \"" + name(entity, stripPrefix, raw) + "\";");
            }

            out.println();

            for (Dependency d : raw.getDependencies()) {
                Entity from = d.getFrom();
                Entity to = d.getTo();

                if (d.getStrength() == CouplingStrength.Implementation && !impl)
                    continue;

                out.println("  \"" + name(from, stripPrefix, raw) + "\" -> \"" + name(to, stripPrefix, raw) + "\"" +
                                    "[weight=\"" + (d.getStrength() == CouplingStrength.Public ? "2" : "1") + "\"];");
            }

            out.println("}");
        }
    }

    private void writeUnusedEntities(Path toFile, Analysis raw, boolean stripPrefix) throws IOException {
        Set<Entity> isolated = new HashSet<>(raw.getEntities());

        for (Dependency d : raw.getDependencies())
            isolated.remove(d.getTo());

        try (BufferedWriter bw = Files.newBufferedWriter(toFile, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("digraph {");
            out.println("  rankdir=LR;");

            for (Entity entity : isolated) {
                out.println("  \"" + name(entity, stripPrefix, raw) + "\";");
            }

            out.println();

            for (Dependency d : raw.getDependencies()) {
                Entity from = d.getFrom();
                Entity to = d.getTo();

                if (isolated.contains(from))
                    out.println("  \"" + name(from, stripPrefix, raw) + "\" -> \"" + name(to, stripPrefix, raw) + "\";");
            }

            out.println("}");
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
