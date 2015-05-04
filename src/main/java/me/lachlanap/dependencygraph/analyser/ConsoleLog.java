package me.lachlanap.dependencygraph.analyser;

/**
* Logs output straight to stdout.
*/
public class ConsoleLog implements Log {
    @Override
    public void info(String message) {
        System.out.println("INFO: " + message);
    }

    @Override
    public void error(String message, Exception error) {
        System.out.println("ERROR: " + message);
        error.printStackTrace(System.out);
    }
}
