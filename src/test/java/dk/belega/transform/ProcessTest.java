package dk.belega.transform;

import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

import static dk.belega.transform.Expect.*;

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
}