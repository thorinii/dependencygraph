package me.lachlanap.dependencygraph.analysis.io;

/**
 *
 * @author Lachlan Phillips
 */
public class LoaderCouldNotFindClassException extends LoadingFailedException {

    public LoaderCouldNotFindClassException(String klass) {
        super(klass + " not found");
    }

}
