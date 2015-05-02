package me.lachlanap.dependencygraph.analyser;

import java.util.function.Function;

@FunctionalInterface
public interface Rewriter extends Function<Entity, Entity> {
}
