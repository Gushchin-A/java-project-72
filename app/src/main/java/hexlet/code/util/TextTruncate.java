package hexlet.code.util;

/** Utility for string formatting. */
public final class TextTruncate {

    /** Max text length. */
    private static final int MAX_LENGTH = 200;

    private TextTruncate() {
    }

    /**
     * Truncates text to preview.
     *
     * @param text source text
     * @return formatted text
     */
    public static String truncate(final String text) {
        if (text == null) {
            return "";
        }
        if (text.length() <= MAX_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_LENGTH) + "...";
    }
}
