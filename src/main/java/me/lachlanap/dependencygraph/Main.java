package me.lachlanap.dependencygraph;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage:\n"
                               + "java -cp ... "
                               + Main.class.getName() + " "
                               + "<output directory> <directory or jar> [<directory or jar>...]");
            DependencyAnalyserConfig config = new DependencyAnalyserConfig();
            config.setOutputPath("out");
            config.addToAnalyse("massive-jar.jar");
            config.setRootProjectPackage("java");
            config.filterCoreJava = false;

            new DependencyAnalyser(config).analyse();
        } else {
            DependencyAnalyserConfig config = new DependencyAnalyserConfig();
            config.setOutputPath(args[0]);

            Arrays.stream(args).skip(1).forEachOrdered(config::addToAnalyse);

            new DependencyAnalyser(config).analyse();
        }
    }
}
