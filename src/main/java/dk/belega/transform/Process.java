package dk.belega.transform;

import java.io.*;

/**
 * Application entry point for XSLT processor
 */
public class Process {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Types, constants

    private static final String USAGE_MESSAGE =
            "Usage: transform [options] <stylesheet> <file>...";

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Data

    private PrintWriter out;

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Application entry point

    public static void main(String[] args) {
        final PrintWriter out = new PrintWriter(System.out);
        try {
            new Process(out).run(args);
        } finally {
            out.flush();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Contruct, copy, destroy

    Process(PrintWriter out) {
        this.out = out;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Operations

    void run(@SuppressWarnings("unused") String[] args) {
        out.println(USAGE_MESSAGE);
    }
}
