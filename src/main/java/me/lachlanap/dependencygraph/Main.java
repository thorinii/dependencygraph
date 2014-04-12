package me.lachlanap.dependencygraph;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import me.lachlanap.dependencygraph.analysis.ProjectAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalyser;
import me.lachlanap.dependencygraph.analysis.analyser.ClassAnalysis;
import me.lachlanap.dependencygraph.analysis.io.JarLoader;
import me.lachlanap.dependencygraph.analysis.io.Parser;
import me.lachlanap.dependencygraph.analysis.io.ThreadSafeLoader;
import me.lachlanap.dependencygraph.analysis.spider.JarSpider;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        URL path = Paths.get("small-jar.jar").toUri().toURL();

        ProjectAnalyser analyser = new ProjectAnalyser(new JarSpider(path),
                                                       new ThreadSafeLoader(new JarLoader(path)),
                                                       new Parser(),
                                                       new ClassAnalyser());
        List<ClassAnalysis> analysis = analyser.analyse();
        System.out.println(analysis);
    }
}
