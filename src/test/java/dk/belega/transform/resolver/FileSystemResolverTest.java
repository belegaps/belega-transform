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

        @Override
        String cwd() {
            return getClass()
                    .getPackage().getName()
                    .replaceAll("^|$|\\.", "/");
        }

        @Override
        String dirname(String path) {
            if (path.contains("/"))
                return path.replaceAll("/[^/]*$", "");
            else
                return ".";
        }

        @Override
        String join(String path, String file) {
            if (file.startsWith("/"))
                if (path.endsWith("/"))
                    return path + file.substring(1);
                else
                    return path + file;
            else if (path.endsWith("/"))
                return path + file;
            else
                return path + "/" + file;
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
    public void testRelativeNoBase() throws TransformerException {

        final String RELATIVE_PATH  = "relative/path/to/resource";
        final String EXPECTED_RESULT = vfs.cwd().concat(RELATIVE_PATH);

        // When resolving a relative resource
        resolver.resolve(RELATIVE_PATH, null);

        // Then the resolver looks up relative to current working directory
        assertEquals(EXPECTED_RESULT, vfs.getPath());
    }

    @Test
    public void testRelativeWithBase() throws TransformerException {

        final String BASE_PATH = "/path/to/";
        final String RELATIVE_PATH = "file";
        final String EXPECTED_RESULT = BASE_PATH + RELATIVE_PATH;

        // When doing a relative lookup with a base
        resolver.resolve(RELATIVE_PATH, FileSystemResolver.SYSTEM_ID_PREFIX + BASE_PATH + "other-file");

        // Then the resource is looked up relative to that base
        assertEquals(EXPECTED_RESULT, vfs.getPath());
    }

    @Test
    public void textAbsoluteNoBase() throws TransformerException {

        final String EXPECTED_RESULT = "/path/to/file";

        // When resolving an absolute path with no base
        resolver.resolve(EXPECTED_RESULT, null);

        // Then the opened file is the absolute path
        assertEquals(EXPECTED_RESULT, vfs.getPath());
    }

    @Test
    public void testAbsoluteWithBase() throws TransformerException {

        final String EXPECTED_RESULT = "/path/to/file";

        // When resolving an absolute path, even with base
        resolver.resolve(EXPECTED_RESULT,
                FileSystemResolver.SYSTEM_ID_PREFIX + "/other/path/to/other/file");

        // Then the opened file is the absolute path
        assertEquals(EXPECTED_RESULT, vfs.getPath());
    }

    @Test
    public void testSystemId() throws TransformerException {

        final String FILE_PATH = "/path/to/file";
        final String EXPECTED_RESULT = FileSystemResolver.SYSTEM_ID_PREFIX + FILE_PATH;

        // When resolving an href
        final Source actualResult = resolver.resolve(FILE_PATH, null);

        // Then the system id is set to the schema + absolute path
        assertEquals(EXPECTED_RESULT, actualResult.getSystemId());
    }

    @Test
    public void testUnhandledBase() throws TransformerException {

        // When resolving an href with a non-file base
        final Source actualResult = resolver.resolve(
                "/path/to/file", "http://www.example.com/path/to/other/file");

        // Then the result is null
        assertNull(actualResult);
    }
}