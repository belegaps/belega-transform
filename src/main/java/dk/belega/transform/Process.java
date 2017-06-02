package dk.belega.transform;

import dk.belega.transform.resolver.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.*;

/**
 * Application entry point for XSLT processor
 */
public class Process {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Types, constants

    private static final String USAGE_MESSAGE =
            "Usage: transform [options] <stylesheet> <file>...\n" +
                    "\n" +
                    "Options\n" +
                    "\n" +
                    "    -p name value\n" +
                    "    --param name value\n" +
                    "        Adds a string parameter for the XSLT processor";

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

    void run(String[] args) {

        if (0 == args.length)
            out.println(USAGE_MESSAGE);
        else {
            Map<String,String> parameters = new HashMap<>();

            int i = 0;
            for (; args[i].startsWith("-"); ++i) {
                switch (args[i]) {
                    case "-p":
                    case "--param":
                        parameters.put(args[i + 1], args[i + 2]);
                        i += 2;
                        break;
                }
            }

            try {
                final Source stylesheet = stylesheetResolver.resolve(args[i], null);
                final Source input = fileResolver.resolve(args[i+1], null);
                final StreamResult output = new StreamResult(out);

                final TransformerFactory transformerFactory = TransformerFactory.newInstance();
                transformerFactory.setURIResolver(stylesheetResolver);

                final Transformer transformer = transformerFactory.newTransformer(stylesheet);

                for (Map.Entry<String, String> parameter : parameters.entrySet())
                    transformer.setParameter(parameter.getKey(), parameter.getValue());

                transformer.transform(input, output);
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }
}
