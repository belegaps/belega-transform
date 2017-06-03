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

    static final String SYSTEM_ID_PREFIX = "file://";

    static class VFS {

        InputStream open(String path) throws FileNotFoundException {
            return new FileInputStream(path);
        }

        String cwd() {
            return new File("").getAbsolutePath();
        }

        boolean isAbsolute(String path) {
            return new File(path).isAbsolute();
        }

        String dirname(String path) {
            return new File(path).getParent();
        }

        String join(String path, String file) {
            return new File(new File(path), file).getPath();
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
            if (null != base && !base.startsWith(SYSTEM_ID_PREFIX))
                return null;

            final String path;
            if (vfs.isAbsolute(href))
                path = href;
            else if (null != base) {
                base = base.substring(SYSTEM_ID_PREFIX.length());
                final String parent = vfs.dirname(base);
                path = vfs.join(parent, href);
            } else
                path = vfs.join(vfs.cwd(), href);

            return new StreamSource(vfs.open(path), SYSTEM_ID_PREFIX + path);
        } catch (FileNotFoundException e) {
            throw new TransformerException("from " + vfs.cwd(), e);
        }
    }
}
