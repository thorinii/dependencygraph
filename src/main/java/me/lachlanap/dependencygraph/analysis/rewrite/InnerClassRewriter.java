package me.lachlanap.dependencygraph.analysis.rewrite;

/**
 *
 * @author Lachlan Phillips
 */
public class InnerClassRewriter implements Rewriter {

    @Override
    public String rewriteClassName(String name) {
        int indexOf$ = name.indexOf('$');
        return indexOf$ > 0
               ? name.substring(0, indexOf$)
               : name;
    }

}
