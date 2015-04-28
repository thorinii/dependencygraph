package me.lachlanap.dependencygraph.analyser.java;

/**
 *
 * @author Lachlan Phillips
 */
public class LoaderCouldNotFindClassException extends LoadingFailedException {

    public LoaderCouldNotFindClassException(String klass) {
        super(klass + " not found");
    }

}
