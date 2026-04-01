package hexlet.code.util;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/** Utility for checking url with Unirest. */
public final class UrlChecker {

    /** All statuses starting from code 400. */
    private static final int STATUS_400_AND_MORE = 400;

    private UrlChecker() {
    }

    /**
     * Builds url check from page response.
     *
     * @param url url entity
     * @return url check entity
     */
    public static UrlCheck check(final Url url) {
        HttpResponse<String> response = Unirest.get(url.getName()).asString();

        int statusCode = response.getStatus();
        if (statusCode >= STATUS_400_AND_MORE) {
            throw new RuntimeException(
                    "Check failed with status: " + statusCode);
        }

        String body = response.getBody();
        Document document = Jsoup.parse(body);

        String title = normalizeText(document.title());
        String h1 = getText(document.selectFirst("h1"));
        String description = getAttribute(document
                .selectFirst(
                        "meta[name=description]"), "content");

        return new UrlCheck(statusCode, title, h1, description, url.getId());
    }

    private static String getText(final Element element) {
        if (element == null) {
            return null;
        }

        return normalizeText(element.text());
    }

    private static String getAttribute(
            final Element element, final String attributeName) {
        if (element == null) {
            return null;
        }

        return normalizeText(element.attr(attributeName));
    }

    private static String normalizeText(final String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        return text;
    }
}
