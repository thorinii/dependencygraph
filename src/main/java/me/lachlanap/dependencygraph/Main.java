package me.lachlanap.dependencygraph;

import me.lachlanap.dependencygraph.analyser.Rewriter;
import me.lachlanap.dependencygraph.analyser.java.ClassAnalyser;
import me.lachlanap.dependencygraph.analyser.java.Loader;
import me.lachlanap.dependencygraph.analyser.java.Parser;
import me.lachlanap.dependencygraph.analyser.java.Spider;
import me.lachlanap.dependencygraph.analyser.java.spider.CompositeSpider;
import me.lachlanap.dependencygraph.analyser.java.spider.DirectorySpider;
import me.lachlanap.dependencygraph.analyser.java.spider.JarSpider;
import me.lachlanap.dependencygraph.io.CompositeLoader;
import me.lachlanap.dependencygraph.io.DirectoryLoader;
import me.lachlanap.dependencygraph.io.JarLoader;

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
        Path out;
        List<Path> toAnalyse;

        if (args.length < 2) {
            System.out.println("Usage:\n"
                                       + "java -cp ... "
                                       + Main.class.getName() + " "
                                       + "<output directory> <directory or jar> [<directory or jar>...]");
            out = path("out");
            toAnalyse = Collections.singletonList(path("target/classes"));
        } else {
            out = path(args[0]);
            toAnalyse = Arrays.stream(args).skip(1).map(Main::path).collect(Collectors.toList());
        }

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

        // TODO: why need this?
        System.exit(0);
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
