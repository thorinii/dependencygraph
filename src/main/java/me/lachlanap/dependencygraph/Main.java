package me.lachlanap.dependencygraph;

import me.lachlanap.dependencygraph.analyser.DependencyAnalyser;
import me.lachlanap.dependencygraph.analyser.Rewriter;
import me.lachlanap.dependencygraph.analyser.java.ClassAnalyser;
import me.lachlanap.dependencygraph.analyser.Loader;
import me.lachlanap.dependencygraph.analyser.java.Parser;
import me.lachlanap.dependencygraph.analyser.Spider;
import me.lachlanap.dependencygraph.analyser.CompositeSpider;
import me.lachlanap.dependencygraph.analyser.DirectorySpider;
import me.lachlanap.dependencygraph.analyser.java.JarSpider;
import me.lachlanap.dependencygraph.analyser.CompositeLoader;
import me.lachlanap.dependencygraph.analyser.DirectoryLoader;
import me.lachlanap.dependencygraph.analyser.java.JarLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage:\n"
                                       + "java -cp ... "
                                       + Main.class.getName() + " "
                                       + "<output directory> <directory or jar> [<directory or jar>...]");
            analyse("out", Collections.singletonList("target/classes"));
        } else {
            analyse(args[0], Arrays.stream(args).skip(1).collect(Collectors.toList()));
        }

        // TODO: why need this?
        System.exit(0);
    }

    private static void analyse(String out, List<String> toAnalyse) throws IOException {
        analyse(path(out), toAnalyse.stream().map(Main::path).collect(Collectors.toList()));
    }

    private static void analyse(Path out, List<Path> toAnalyse) throws IOException {
        Spider spider = new CompositeSpider(
                toAnalyse.stream().map(Main::spiderFor).collect(Collectors.toList()));
        Loader loader = new CompositeLoader(
                toAnalyse.stream().map(Main::loaderFor).collect(Collectors.toList()));

        DependencyAnalyser da = new DependencyAnalyser(spider,
                                                       loader,
                                                       new Parser(),
                                                       new ClassAnalyser(),
                                                       INNER_CLASS_REWRITER);

        da.analyse(out, true);
    }

    private static Path path(String p) {
        return Paths.get(p);
    }

    private static final Rewriter INNER_CLASS_REWRITER = e -> {
        int index = e.getName().indexOf('$');
        if (index >= 0) {
            return e.changeName(e.getName().substring(0, index));
        } else
            return e;
    };

    private static Spider spiderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectorySpider(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar"))
            return new JarSpider(toAnalyse);
        else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }

    private static Loader loaderFor(Path toAnalyse) {
        if (Files.isDirectory(toAnalyse))
            return new DirectoryLoader(toAnalyse);
        else if (toAnalyse.toString().toLowerCase().endsWith(".jar")) {
            JarLoader loader = new JarLoader(toAnalyse);
            loader.init();
            return loader;
        } else
            throw new UnsupportedOperationException("Don't know how to read " + toAnalyse);
    }
}
