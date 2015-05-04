package me.lachlanap.dependencygraph.analyser;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Lachlan Phillips
 */
public class CompositeLoader implements Loader {

    private final List<Loader> loaders;

    public CompositeLoader(List<Loader> loaders) {
        if (loaders.isEmpty())
            throw new IllegalArgumentException("Must have at least one loader");
        this.loaders = loaders;
    }

    @Override
    public byte[] load(String path) throws LoadingFailedException {
        return loaders.stream()
                .map(l -> tryCatching((Loader loader) -> loader.load(path), LoaderCouldNotFindClassException.class)
                        .apply(l))
                .filter(o -> o.isPresent())
                .map(o -> o.get())
                .findFirst()
                .orElseThrow(() -> new LoaderCouldNotFindClassException("Could not find " + path));
    }

    private <A, R, T extends RuntimeException> Function<A, Optional<R>> tryCatching(Function<A, R> f, Class<T>... catching) throws T {
        return (A a) -> {
            try {
                return Optional.of(f.apply(a));
            } catch (Exception e) {
                for (Class<T> toCatch : catching)
                    if (toCatch.isInstance(e))
                        return Optional.empty();
                throw e;
            }
        };
    }

    @Override
    public void close() {
        loaders.forEach(Loader::close);
    }
}
