package me.lachlanap.dependencygraph.analyser;

/**
 *
 * @author Lachlan Phillips
 */
public class LoaderCouldNotFindClassException extends LoadingFailedException {

    public LoaderCouldNotFindClassException(String klass) {
        super(klass + " not found");
    }

}
