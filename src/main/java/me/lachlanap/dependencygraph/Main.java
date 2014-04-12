package me.lachlanap.dependencygraph;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import me.lachlanap.dependencygraph.analysis.ProjectAnalyser;
import me.lachlanap.dependencygraph.analysis.ProjectAnalysis;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.PackageAnalyser;
import me.lachlanap.dependencygraph.analysis.filter.ExcludingPackageFilter;
import me.lachlanap.dependencygraph.analysis.io.JarLoader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.rewrite.InnerClassRewriter;
import me.lachlanap.dependencygraph.analysis.spider.JarSpider;
import me.lachlanap.dependencygraph.diagram.ClassDiagram;
import me.lachlanap.dependencygraph.diagram.DiagramWriter;
import me.lachlanap.dependencygraph.diagram.PackageDiagram;
import me.lachlanap.dependencygraph.diagram.PartitionedClassDiagram;

public class Main {

    public static void main(String[] args) {
        URL path = Util.pathToUrl(Paths.get("eatit-android.jar"));
        Path out = Paths.get("out");

        ProjectAnalyser analyser = new ProjectAnalyser(
                new JarSpider(path),
                new ThreadSafeLoader(new JarLoader(path)),
                new Parser(),
                new ClassAnalyser(),
                new PackageAnalyser(),
                new InnerClassRewriter());

        System.out.println("Analysing jar");
        ProjectAnalysis analysis = analyser.analyse();

        System.out.println("Filtering results");
        //analysis = analysis.keepOnly(new PackageFilter("java", "javax"));
        //analysis = analysis.keepOnly(new PackageFilter("com", "sun", "sunw", "org"));
        //analysis = analysis.keepOnly(new ExcludingPackageFilter("java", "javax.util", "android"));
        analysis = analysis.keepOnly(new ExcludingPackageFilter("com.planticle").invert());


        Util.createBlankDirectory(out);

        System.out.println("Generating diagrams");
        DiagramWriter writer = new DiagramWriter(out);

        System.out.println("Writing package diagram");
        writer.writeDiagram("package.dot", new PackageDiagram(analysis));

        System.out.println("Writing class diagrams");
        writer.writeDiagram("class.dot", new ClassDiagram(analysis));
        writer.writeDiagram("class-partitioned.dot", new PartitionedClassDiagram(analysis));
    }
}
