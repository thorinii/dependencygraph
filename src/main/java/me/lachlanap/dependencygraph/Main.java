package me.lachlanap.dependencygraph;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.lachlanap.dependencygraph.analysis.ProjectAnalyser;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalyser;
import me.lachlanap.dependencygraph.analysis.filter.PackageFilter;
import me.lachlanap.dependencygraph.analysis.io.JarLoader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.spider.JarSpider;
import me.lachlanap.dependencygraph.diagram.ClassDiagram;
import me.lachlanap.dependencygraph.diagram.DiagramWriter;
import me.lachlanap.dependencygraph.diagram.PackageDiagram;

public class Main {

    public static void main(String[] args) {
        URL path = Util.pathToUrl(Paths.get("small-jar.jar"));
        Path out = Paths.get("out");

        ProjectAnalyser analyser = new ProjectAnalyser(
                new JarSpider(path),
                new ThreadSafeLoader(new JarLoader(path)),
                new Parser(),
                new ClassAnalyser(),
                new PackageAnalyser());
        ProjectAnalysis analysis = analyser.analyse()
                .keepOnly(new PackageFilter("java",
                                            "javax"));


        Util.createBlankDirectory(out);

        DiagramWriter writer = new DiagramWriter(out);
        writer.writeDiagram("class.dot", new ClassDiagram(analysis));
        writer.writeDiagram("package.dot", new PackageDiagram(analysis));
    }
}
