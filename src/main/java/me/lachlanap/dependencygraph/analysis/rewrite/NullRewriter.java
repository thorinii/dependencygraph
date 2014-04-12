package me.lachlanap.dependencygraph.analysis.rewrite;

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
