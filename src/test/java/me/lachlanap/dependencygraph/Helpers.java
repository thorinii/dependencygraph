package me.lachlanap.dependencygraph;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import me.lachlanap.dependencygraph.analyser.java.ClassFileBuilder;
import me.lachlanap.dependencygraph.analyser.java.ProjectAnalysis;
import me.lachlanap.dependencygraph.analyser.java.ClassAnalysis;
import me.lachlanap.dependencygraph.analyser.java.PackageAnalysis;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

/**
 *
 * @author Lachlan Phillips
 */
public class Helpers {

    public static byte[] loadClassFile(String resource) {
        try (InputStream is = Helpers.class.getResourceAsStream("/" + resource + ".class");
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buf = new byte[1024];
            int read;
            while ((read = is.read(buf)) != -1)
                bos.write(buf, 0, read);
            return bos.toByteArray();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static ClassAnalysis emptyClassAnalysis(String name) {
        return new ClassAnalysis(new ClassFileBuilder(name).build(), Collections.emptySet());
    }

    public static ClassAnalysis classAnalysis(String name, String... dependencies) {
        return new ClassAnalysis(new ClassFileBuilder(name).build(), new HashSet<>(Arrays.asList(dependencies)));
    }

    public static PackageAnalysis packageAnalysis(String name, String... dependencies) {
        return new PackageAnalysis(name, new HashSet<>(Arrays.asList(dependencies)));
    }

    public static ProjectAnalysis projectAnalysisOfClasses(ClassAnalysis... classes) {
        return new ProjectAnalysis("", Arrays.asList(classes), Collections.emptyList());
    }

    public static <T, V> Matcher<T> that(Function<T, V> lambda, Matcher<V> matcher) {
        return new TypeSafeDiagnosingMatcher<T>() {
            @Override
            protected boolean matchesSafely(T item, Description mismatchDescription) {
                if (matcher.matches(lambda.apply(item)))
                    return true;
                else {
                    matcher.describeMismatch(lambda.apply(item), mismatchDescription);
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an object with ")
                        .appendDescriptionOf(matcher);
            }
        };
    }

    public static <T> Matcher<Collection<? super T>> hasOnly(T... items) {
        return new TypeSafeDiagnosingMatcher<Collection<? super T>>() {

            @Override
            protected boolean matchesSafely(Collection<? super T> collection, Description mismatchDescription) {
                List<T> check = Arrays.asList(items);
                if (collection.containsAll(check) && check.containsAll(collection)) {
                    return true;
                } else {
                    List<? super T> extra = new ArrayList<>(collection);
                    extra.removeAll(check);

                    List<? super T> missing = new ArrayList<>(check);
                    missing.removeAll(collection);

                    if (extra.size() > 0)
                        mismatchDescription.appendText("included: ")
                                .appendValueList("[", ", ", "] ", extra);
                    if (missing.size() > 0)
                        mismatchDescription.appendText(", excluded: ")
                                .appendValueList("[", ", ", "]", missing);
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description
                        .appendText("a collection containing only ")
                        .appendValueList("[", ", ", "]", items);
            }
        };
    }

    public static void extractJar(Path jar, Path to) throws IOException {
        Util.createBlankDirectory(to);

        try (JarInputStream stream = new JarInputStream(Files.newInputStream(jar))) {
            JarEntry entry;

            while ((entry = stream.getNextJarEntry()) != null) {
                String name = (entry.getName().startsWith("/")) ? entry.getName().substring(1) : entry.getName();

                if (entry.isDirectory()) {
                    Files.createDirectories(to.resolve(name));
                } else {
                    Files.copy(stream, to.resolve(name));
                }
            }
        }
    }

    public static void deleteExtracted(Path to) throws IOException {
        Util.createBlankDirectory(to);
        Files.delete(to);
    }
}
