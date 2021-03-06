package me.lachlanap.dependencygraph.analyser;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Lachlan Phillips
 */
public class CompositeSpider implements Spider {

    private final List<Spider> spiders;

    public CompositeSpider(List<Spider> spiders) {
        this.spiders = spiders;
    }

    @Override
    public List<String> findClassesToAnalyse() throws SpiderException {
        return spiders.stream()
                .flatMap((Spider s) -> s.findClassesToAnalyse().stream())
                .collect(Collectors.toList());
    }

}
