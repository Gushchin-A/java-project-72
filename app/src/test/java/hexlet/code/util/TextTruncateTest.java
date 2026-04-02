package hexlet.code.util;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextTruncateTest {
    /** Test long text. */
    private static final String LONG_TEXT = "long-string.txt";
    /** Text after truncate. */
    private static final String TRUNCATE_TEXT = "truncate-text.txt";


    @Test
    void testTruncateLongText() throws IOException {
        String longText = readFixture(LONG_TEXT).trim();
        String expected = readFixture(TRUNCATE_TEXT).trim();

        assertEquals(expected, TextTruncate.truncate(longText));
    }

    private String readFixture(final String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("fixtures/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Fixture not found: " + fileName);
            }
            return new String(
                    inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
