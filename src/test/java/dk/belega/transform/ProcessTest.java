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

        // Given an XML document resolver
        process.setFileResolver(this::resourceResolver);

        // And an identity XSLT
        process.setStylesheetResolver(this::resourceResolver);

        // When transforming an XML document using an identify stylesheet
        process.run(new String[]{
                "identity.xsl",
                EXPECTED_RESULT
        });

        // Then the output is an equal XML document
        expect(out.toString())
                .equalTo(resourceAsString(EXPECTED_RESULT));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Implementation

    @SuppressWarnings("unused")
    private Source resourceResolver(String href, String base) {
        return new StreamSource(getClass().getResourceAsStream(href));
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