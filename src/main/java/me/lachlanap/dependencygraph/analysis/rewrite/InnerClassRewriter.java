package me.lachlanap.dependencygraph.analysis.rewrite;

/**
 *
 * @author Lachlan Phillips
 */
public class InnerClassRewriter implements Rewriter {

    @Override
    public String rewriteClassName(String name) {
        if (name.contains("$"))
            return name.substring(0, name.indexOf('$'));
        else
            return name;
    }

}
