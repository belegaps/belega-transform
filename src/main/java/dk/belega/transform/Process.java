package dk.belega.transform;

import dk.belega.transform.resolver.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
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

    private URIResolver fileResolver =
            new FileSystemResolver();

    private URIResolver stylesheetResolver =
            new FileSystemResolver();

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
    // Properties

    void setFileResolver(URIResolver resolver) {
        this.fileResolver = resolver;
    }

    void setStylesheetResolver(URIResolver resolver) {
        this.stylesheetResolver = resolver;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Operations

    void run(@SuppressWarnings("unused") String[] args) {
        if (0 == args.length)
            out.println(USAGE_MESSAGE);
        else {
            try {
                final Source stylesheet = stylesheetResolver.resolve(args[0], null);
                final Source input = fileResolver.resolve(args[1], null);
                final StreamResult output = new StreamResult(out);

                final Transformer transformer = TransformerFactory.newInstance().newTransformer(stylesheet);
                transformer.transform(input, output);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }
}
