package me.lachlanap.dependencygraph;

import me.lachlanap.dependencygraph.analyser.*;
import me.lachlanap.dependencygraph.analyser.java.*;
import me.lachlanap.dependencygraph.analyser.java.rewrite.InnerClassRewriter;
import me.lachlanap.dependencygraph.analyser.java.spider.CompositeSpider;
import me.lachlanap.dependencygraph.analyser.java.spider.DirectorySpider;
import me.lachlanap.dependencygraph.analyser.java.spider.JarSpider;
import me.lachlanap.dependencygraph.diagram.*;
import me.lachlanap.dependencygraph.io.CompositeLoader;
import me.lachlanap.dependencygraph.io.DirectoryLoader;
import me.lachlanap.dependencygraph.io.JarLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Lachlan Phillips
 */
public class DependencyAnalyser {

    private final DependencyAnalyserConfig config;

    public DependencyAnalyser(DependencyAnalyserConfig config) {
        this.config = config;
    }

    public void analyse() throws IOException {
        System.out.println("Initialising");
        ProjectAnalyser analyser = buildAnalyser(config.toAnalyse, config.rootProjectPackage);

        System.out.println("Analysing project");
        Analysis raw = analyser.analyse();

        if (config.filterCoreJava()) { // TODO: refactor this into Java specific
            raw = new AnalysisBuilder(raw).removeDependencies("java.").build();
        }

        ProjectAnalysis analysis = new ProjectAnalysis(raw);

        System.out.println("Generating diagrams");
        generateDiagrams(config.outputPath, analysis);

        System.out.println("Dumping raw analysis");
        dumpRaw(config.outputPath.resolve("raw.json"), raw);

        System.out.println("Done");
    }

    private ProjectAnalyser buildAnalyser(List<Path> toAnalyse, Optional<String> rootPackageOverride) {
        Spider spider = new CompositeSpider(
                toAnalyse.stream().map(this::spiderFor).collect(Collectors.toList()));
        ThreadSafeLoader loader = new CompositeLoader(
                toAnalyse.stream().map(this::loaderFor).collect(Collectors.toList()));

        return new ProjectAnalyser(
                spider,
                loader,
                new Parser(),
                new ClassAnalyser(),
                new PackageAnalyser(),
                new InnerClassRewriter(),
                rootPackageOverride);
    }

    private Spider spiderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectorySpider(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar"))
            return new JarSpider(toAnalyse);
        else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }

    private ThreadSafeLoader loaderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectoryLoader(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar")) {
            JarLoader loader = new JarLoader(toAnalyse);
            loader.init();
            return loader;
        } else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }

    private void dumpRaw(Path to, Analysis raw) throws IOException {
        Map<String, Integer> entityIds = new HashMap<>();
        int nextId = 0;
        try (BufferedWriter bw = Files.newBufferedWriter(to, StandardCharsets.UTF_8);
             PrintWriter out = new PrintWriter(bw)) {
            out.println("{\"entities\": [");

            for (Entity entity : raw.getEntities()) {
                out.println("  \"" + entity.getName() + "\",");
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

        System.out.println(raw);
    }

    private void generateDiagrams(Path out, ProjectAnalysis analysis) throws DiagramWritingException {
        Util.createBlankDirectory(out);

        DiagramWriter writer = new DiagramWriter(out);

        writer.writeDiagram("packages.dot", new PackageDiagram(analysis));
        writer.writeDiagram("classes.dot", new ClassDiagram(analysis));
        writer.writeDiagram("classes-partitioned.dot", new PartitionedClassDiagram(analysis));


        ProjectAnalysis projectClasses = analysis.keepOnlyProjectClasses();

        writer.writeDiagram("project-packages.dot", new PackageDiagram(projectClasses));
        writer.writeDiagram("project-classes.dot", new ClassDiagram(projectClasses));
        writer.writeDiagram("project-classes-partitioned.dot", new PartitionedClassDiagram(projectClasses));

        projectClasses.getPackageAnalysis().parallelStream().forEach(pack -> {
            ProjectAnalysis filteredByPackage = analysis.keepOnlyAnalysisMatching(p -> p.equals(pack.getName()));

            writer.writeDiagram("project-classes-partitioned-" + pack.getName() + ".dot",
                                new PartitionedClassDiagram(filteredByPackage));
        });
    }
}
