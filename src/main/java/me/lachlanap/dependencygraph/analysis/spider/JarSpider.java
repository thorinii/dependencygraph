package me.lachlanap.dependencygraph.analysis.spider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class JarSpider implements Spider {

    private final Path jar;

    public JarSpider(Path jar) {
        this.jar = jar;
    }

    @Override
    public List<String> findClassesToAnalyse() throws SpiderException {
        try (JarInputStream stream = new JarInputStream(Files.newInputStream(jar))) {
            JarEntry entry;
            List<String> classes = new ArrayList<>();

            while ((entry = stream.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class"))
                    classes.add(fileToClassName(entry.getName()));
            }

            return classes;
        } catch (IOException ioe) {
            throw new SpiderException("Could not read jar " + jar, ioe);
        }
    }

    private String fileToClassName(String name) {
        return name.substring(0, name.lastIndexOf(".class"))
                .replaceAll("/", ".");
    }
}
