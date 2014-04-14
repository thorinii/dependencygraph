package me.lachlanap.dependencygraph;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import me.lachlanap.dependencygraph.analysis.ProjectAnalyser;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalyser;
import me.lachlanap.dependencygraph.analysis.filter.IncludingPackageFilter;
import me.lachlanap.dependencygraph.analysis.io.*;
import me.lachlanap.dependencygraph.analysis.rewrite.InnerClassRewriter;
import me.lachlanap.dependencygraph.analysis.spider.CompositeSpider;
import me.lachlanap.dependencygraph.analysis.spider.DirectorySpider;
import me.lachlanap.dependencygraph.analysis.spider.JarSpider;
import me.lachlanap.dependencygraph.analysis.spider.Spider;
import me.lachlanap.dependencygraph.diagram.*;

/**
 *
 * @author Lachlan Phillips
 */
public class DependencyAnalyser {

    private final DependencyAnalyserConfig config;

    public DependencyAnalyser(DependencyAnalyserConfig config) {
        this.config = config;
    }

    public void analyse() {
        ProjectAnalyser analyser = buildAnalyser(config.toAnalyse);

        System.out.println("Analysing jar");
        ProjectAnalysis analysis = analyser.analyse();

        System.out.println("Filtering results");
        analysis = analysis.keepOnly(new IncludingPackageFilter("java", "javax").invert());

        System.out.println("Generating diagrams");
        generateDiagrams(config.outputPath, analysis);
    }

    private ProjectAnalyser buildAnalyser(List<Path> toAnalyse) {
        Spider spider = new CompositeSpider(
                toAnalyse.stream().map(this::spiderFor).collect(Collectors.toList()));
        Loader loader = new CompositeLoader(
                toAnalyse.stream().map(this::loaderFor).collect(Collectors.toList()));

        ProjectAnalyser analyser = new ProjectAnalyser(
                spider,
                new ThreadSafeLoader(loader),
                new Parser(),
                new ClassAnalyser(),
                new PackageAnalyser(),
                new InnerClassRewriter());
        return analyser;
    }

    private Spider spiderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectorySpider(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar"))
            return new JarSpider(toAnalyse);
        else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }

    private Loader loaderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectoryLoader(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar"))
            return new JarLoader(toAnalyse);
        else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }

    private void generateDiagrams(Path out, ProjectAnalysis analysis) throws DiagramWritingException {
        Util.createBlankDirectory(out);

        DiagramWriter writer = new DiagramWriter(out);

        writer.writeDiagram("packages.dot", new PackageDiagram(analysis));
        writer.writeDiagram("classes.dot", new ClassDiagram(analysis));
        writer.writeDiagram("classes-partitioned.dot", new PartitionedClassDiagram(analysis));


        analysis = analysis.keepOnlyProjectClasses();

        writer.writeDiagram("project-packages.dot", new PackageDiagram(analysis));
        writer.writeDiagram("project-classes.dot", new ClassDiagram(analysis));
        writer.writeDiagram("project-classes-partitioned.dot", new PartitionedClassDiagram(analysis));
    }
}
