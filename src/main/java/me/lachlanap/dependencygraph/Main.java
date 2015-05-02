package me.lachlanap.dependencygraph;

import java.io.IOException;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        DependencyAnalyserConfig config;

        if (args.length < 2) {
            System.out.println("Usage:\n"
                               + "java -cp ... "
                               + Main.class.getName() + " "
                               + "<output directory> <directory or jar> [<directory or jar>...]");
            config = new DependencyAnalyserConfig();
            config.setOutputPath("out");
            config.addToAnalyse("target/classes");
        } else {
            config = new DependencyAnalyserConfig();
            config.setOutputPath(args[0]);

            Arrays.stream(args).skip(1).forEachOrdered(config::addToAnalyse);
        }

        new DependencyAnalyser(config).analyse();
        System.exit(0);
    }
}
