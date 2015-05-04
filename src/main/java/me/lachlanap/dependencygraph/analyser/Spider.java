package me.lachlanap.dependencygraph.analyser;

import me.lachlanap.dependencygraph.analyser.java.SpiderException;

import java.util.List;

/**
 *
 * @author Lachlan Phillips
 */
public interface Spider {

    public List<String> findClassesToAnalyse() throws SpiderException;
}
