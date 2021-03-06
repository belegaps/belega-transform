package dk.belega.transform;

import org.junit.*;

import static org.junit.Assert.assertEquals;

/**
 * Helper class for expectations
 */
class Expect {

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Types, constants

    static class StringExpector{

        private String value;

        StringExpector(String value) {
            this.value = value;
        }

        @SuppressWarnings("UnusedReturnValue")
        StringExpector prefixedBy(@SuppressWarnings("SameParameterValue") String prefix) {
            if (!value.startsWith(prefix)) {
                throw new ComparisonFailure("string prefix differ",
                        prefix, value.substring(0, Math.min(value.length(), prefix.length())));
            }
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        StringExpector equalTo(String expected) {
            assertEquals(expected, value);
            return this;
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // Construct, copy, destroy

    static StringExpector expect(String value) {
        return new StringExpector(value);
    }
}
