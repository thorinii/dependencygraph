package me.lachlanap.dependencygraph;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import me.lachlanap.dependencygraph.analysis.ClassAnalyser;
import me.lachlanap.dependencygraph.io.Loader;
import me.lachlanap.dependencygraph.io.Parser;
import me.lachlanap.dependencygraph.io.ResourceLoader;

public class Main {

    public static void main(String[] args) {
        Loader loader = new ResourceLoader();
        Parser parser = new Parser();
        ClassAnalyser analyser = new ClassAnalyser();

        Arrays.asList("").stream()
                .map(loader::load)
                .map(parser::parse)
                .map(analyser::analyse)
                .forEach(System.out::println);
    }

    private static URL string2Url(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }
}
