package me.lachlanap.dependencygraph.analyser;

/**
 * Created by lachlan on 28/04/15.
 */
public enum CouplingStrength {
    Implementation, Public;

    public boolean strongerThan(CouplingStrength b) {
        return compareTo(b) > 0;
    }
}
