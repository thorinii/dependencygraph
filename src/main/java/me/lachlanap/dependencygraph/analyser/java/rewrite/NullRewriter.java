package me.lachlanap.dependencygraph.analyser.java.rewrite;

import me.lachlanap.dependencygraph.analyser.java.Rewriter;

/**
 *
 * @author Lachlan Phillips
 */
public class NullRewriter implements Rewriter {

    @Override
    public String rewriteClassName(String name) {
        return name;
    }

}
