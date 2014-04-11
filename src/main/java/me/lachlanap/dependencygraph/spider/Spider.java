package me.lachlanap.dependencygraph.spider;

import java.util.List;

/**
 *
 * @author Lachlan Phillips
 */
public interface Spider {

    public List<String> findClassesToAnalyse() throws SpiderException;
}
