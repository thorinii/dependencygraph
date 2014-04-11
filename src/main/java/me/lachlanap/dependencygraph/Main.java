package me.lachlanap.dependencygraph;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import me.lachlanap.dependencygraph.analysis.ClassAnalyser;
import me.lachlanap.dependencygraph.io.JarLoader;
import me.lachlanap.dependencygraph.io.Loader;
import me.lachlanap.dependencygraph.io.Parser;
import me.lachlanap.dependencygraph.spider.JarSpider;
import me.lachlanap.dependencygraph.spider.Spider;

public class Main {

    public static void main(String[] args) throws MalformedURLException {
        URL path = Paths.get("small-jar.jar").toUri().toURL();

        Spider spider = new JarSpider(path);
        Loader loader = new JarLoader(path);
        Parser parser = new Parser();
        ClassAnalyser analyser = new ClassAnalyser();

        spider.findClassesToAnalyse().stream()
                .map(loader::load)
                .map(parser::parse)
                .map(analyser::analyse)
                .forEach(System.out::println);

        loader.close();
    }
}
