package dk.belega.transform.resolver;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

/**
 * URI resolver for file system resources.
 */
public class FileSystemResolver implements URIResolver {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Types, constants

    static class VFS {
        InputStream open(String path) throws FileNotFoundException {
            return new FileInputStream(path);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Data

    private final VFS vfs;

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Construct, copy, destroy

    public FileSystemResolver() {
        this(new VFS());
    }

    FileSystemResolver(VFS vfs) {
        this.vfs = vfs;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Operations

    /**
     * Called by the processor when it encounters
     * an xsl:include, xsl:import, or document() function.
     *
     * @param href An href attribute, which may be relative or absolute.
     * @param base The base URI against which the first argument will be made
     *             absolute if the absolute URI is required.
     * @return A Source object, or null if the href cannot be resolved,
     * and the processor should try to resolve the URI itself.
     * @throws TransformerException if an error occurs when trying to
     *                              resolve the URI.
     */
    @Override
    public Source resolve(String href, String base) throws TransformerException {
        try {
            return new StreamSource(vfs.open(href));
        } catch (FileNotFoundException e) {
            final String cwd = new File("").getAbsolutePath();
            throw new TransformerException("from " + cwd, e);
        }
    }
}
