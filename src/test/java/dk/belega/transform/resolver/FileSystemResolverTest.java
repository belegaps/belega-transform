package dk.belega.transform.resolver;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import javax.xml.transform.*;
import java.io.*;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link FileSystemResolver} class.
 */
@RunWith(JUnit4.class)
public class FileSystemResolverTest {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Types, constants

    private static class TestVFS extends FileSystemResolver.VFS {

        //////////////////////////////////////////////////////////////////////////////////////////
        // Types, constants

        private static final InputStream EMPTY_STREAM =
                new ByteArrayInputStream(new byte[0]);

        //////////////////////////////////////////////////////////////////////////////////////////
        // Data

        private String path = null;

        //////////////////////////////////////////////////////////////////////////////////////////
        // Properties

        private String getPath() {
            return path;
        }

        //////////////////////////////////////////////////////////////////////////////////////////
        // Operations

        @Override
        public InputStream open(String path) throws FileNotFoundException {
            this.path = path;
            return EMPTY_STREAM;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Data

    private TestVFS vfs = new TestVFS();

    private FileSystemResolver resolver =
            new FileSystemResolver(vfs);

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Test cases

    @Test
    public void testRelativeResolve() throws TransformerException {

        final String EXPECTED_RESULT = "relative/path/to/resource";

        // When resolving a relative resource
        resolver.resolve(EXPECTED_RESULT, null);

        // Then the resolver looks up relative to current working directory
        assertEquals(EXPECTED_RESULT, vfs.getPath());
    }
}