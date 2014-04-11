package me.lachlanap.dependencygraph.io;

import java.io.InputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class ResourceLoader extends AbstractIOLoader {

    @Override
    protected InputStream openStream(String path) {
        InputStream stream = getClass().getResourceAsStream("/" + path + ".class");
        if (stream == null)
            throw new LoaderCouldNotFindClassException(path);
        else
            return stream;
    }

}
