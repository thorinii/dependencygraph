package me.lachlanap.dependencygraph.analysis.io;

/**
 *
 * @author Lachlan Phillips
 */
public class ThreadSafeLoader implements Loader {

    private final Loader delegate;

    public ThreadSafeLoader(Loader delegate) {
        this.delegate = delegate;
    }

    @Override
    public synchronized byte[] load(String path) throws LoadingFailedException {
        return delegate.load(path);
    }

    @Override
    public synchronized void close() {
        delegate.close();
    }
}
