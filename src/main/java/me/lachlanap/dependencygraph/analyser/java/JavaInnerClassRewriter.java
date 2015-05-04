package me.lachlanap.dependencygraph.analyser.java;

import me.lachlanap.dependencygraph.analyser.Entity;
import me.lachlanap.dependencygraph.analyser.Rewriter;

/**
 * Renames inner classes to the name of their parent.
 */
public class JavaInnerClassRewriter implements Rewriter {
    @Override
    public Entity apply(Entity e) {
        int index = e.getName().indexOf('$');
        if (index >= 0) {
            return e.changeName(e.getName().substring(0, index));
        } else
            return e;
    }
}
