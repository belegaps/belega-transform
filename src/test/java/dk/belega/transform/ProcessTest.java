package dk.belega.transform;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.io.*;

import static dk.belega.transform.Expect.*;
import static org.junit.Assert.*;

/**
 * Unit tests for the {@link Process} class.
 */
@RunWith(JUnit4.class)
public class ProcessTest {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Data

    private StringWriter out;

    private Process process;

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Test fixture

    @Before
    public void setUp() {
        out = new StringWriter();

        process = new Process(new PrintWriter(out));
        process.setFileResolver(this::resourceResolver);
        process.setStylesheetResolver(this::resourceResolver);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Test cases

    @Test
    public void testUsage() {

        // When running a process with no parameters
        process.run(new String[0]);

        // Then the process outputs usage information
        expect(out.toString())
                .prefixedBy("Usage: ");
    }

    @Test
    public void testTransform() {

        final String EXPECTED_RESULT = "simple.xml";

        // When transforming an XML document using an identify stylesheet
        process.run(new String[]{
                "identity.xsl",
                EXPECTED_RESULT
        });

        // Then the output is an equal XML document
        expect(out.toString())
                .equalTo(resourceAsString(EXPECTED_RESULT));
    }

    @Test
    public void testParameter() {

        final String EXPECTED_VALUE = "some-value";
        final String OTHER_VALUE = "other-value";

        // When passing a parameter to the processor
        process.run(new String[] {
                "-p",
                "some-parameter",
                EXPECTED_VALUE,
                "--param",
                "other-parameter",
                OTHER_VALUE,
                "parameter.xsl",
                "simple.xml"
        });

        // Then the parameter is passed to the XSLT
        expect(out.toString())
                .equalTo(EXPECTED_VALUE + OTHER_VALUE);
    }

    @Test
    public void testIncludedStylesheet() {

        final String EXPECTED_VALUE = "expected-value";

        // When an included stylesheet outputs the value of a parameter
        process.run(new String[] {
                "--param",
                "expected-param",
                EXPECTED_VALUE,
                "include.xsl",
                "simple.xml"
        });

        // Then the result equals the parameter value
        expect(out.toString())
                .equalTo(EXPECTED_VALUE);
    }

    @Test
    public void testIncludedDocument() {

        final String EXPECTED_RESULT = "Document Title";

        // When a stylesheet includes a document
        process.run(new String[] {
                "--param",
                "document-name",
                "title-file.xml",
                "document.xsl",
                "simple.xml"
        });

        // Then the document is loaded from the file resolver
        expect(out.toString())
                .equalTo(EXPECTED_RESULT);
    }

    @Test
    public void testSubFolderInclude() {

        final String EXPECTED_RESULT = "Sub-folder include";

        process.run(new String[] {
                "-p",
                "expected-param",
                EXPECTED_RESULT,
                "sub/major.xsl",
                "simple.xml"
        });

        expect(out.toString())
                .equalTo(EXPECTED_RESULT);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation

    @SuppressWarnings("unused")
    private Source resourceResolver(String href, String base) {

        final String path;
        if (href.startsWith("/"))
            path = href;
        else if (null != base)
            path =  base.replaceAll("jar://", "").replaceAll("/[^/]*$", "/" + href);
        else
            path = getClass().getPackage().getName().replaceAll("^|$|\\.", "/") + href;
        return new StreamSource(getClass().getResourceAsStream(path), "jar://" + path);
    }

    private String resourceAsString(String href) {

        try (final BufferedReader reader = resourceReader(href)) {
            StringBuilder stringBuilder = new StringBuilder();
            for (;;) {
                final int ch = reader.read();
                if (ch < 0)
                    break;

                stringBuilder.append((char)ch);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            fail("Failed to read resource " + href);
            return "";
        }
    }

    private BufferedReader resourceReader(String href) {
        return new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(href)));
    }
}