package me.lachlanap.dependencygraph.analysis.io;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Lachlan Phillips
 */
public class ResourceLoader extends AbstractIOLoader {

    @Override
    protected byte[] safeLoad(String path) throws LoadingFailedException, IOException {
        try (InputStream stream = getClass().getResourceAsStream("/" + path + ".class")) {
            return readFromStream(stream);
        }
    }

}
