package me.lachlanap.dependencygraph;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

        new DependencyAnalyser().analyse(out, toAnalyse, true);

        // TODO: why need this?
        System.exit(0);
    }

    private static Path path(String p) {
        return Paths.get(p);
    }
}
