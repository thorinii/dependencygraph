package me.lachlanap.dependencygraph.spider;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class JarSpider implements Spider {

    private final URL jarUrl;

    public JarSpider(URL jarUrl) {
        this.jarUrl = jarUrl;
    }

    @Override
    public List<String> findClassesToAnalyse() throws SpiderException {
        try (JarInputStream stream = new JarInputStream(jarUrl.openStream())) {
            JarEntry entry;
            List<String> classes = new ArrayList<>();

            while ((entry = stream.getNextJarEntry()) != null) {
                if (entry.getName().endsWith(".class"))
                    classes.add(fileToClassName(entry.getName()));
            }

            return classes;
        } catch (IOException ioe) {
            throw new SpiderException("Could not read jar " + jarUrl, ioe);
        }
    }

    private String fileToClassName(String name) {
        return name.substring(0, name.lastIndexOf(".class"))
                .replaceAll("/", ".");
    }
}
