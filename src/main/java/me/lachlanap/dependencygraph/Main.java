package me.lachlanap.dependencygraph;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        DependencyAnalyserConfig config;

        if (args.length < 3) {
            System.out.println("Usage:\n"
                               + "java -cp ... "
                               + Main.class.getName() + " "
                               + "<project package> <output directory> <directory or jar> [<directory or jar>...]");
            config = new DependencyAnalyserConfig();
            config.setOutputPath("out");
            config.addToAnalyse("target/classes");
            config.setRootProjectPackage("me.lachlanap.dependencygraph");
        } else {
            config = new DependencyAnalyserConfig();
            config.setRootProjectPackage(args[0]);
            config.setOutputPath(args[1]);

            Arrays.stream(args).skip(2).forEachOrdered(config::addToAnalyse);
        }

        new DependencyAnalyser(config).analyse();
        System.exit(0);
    }
}
