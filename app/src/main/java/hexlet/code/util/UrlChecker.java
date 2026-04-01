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
        String title = document.title();

        Element h1Element = document.selectFirst("h1");
        String h1 = h1Element != null ? h1Element.text() : "";

        Element descriptionElement = document
                .selectFirst("meta[name=description]");
        String description = descriptionElement != null
                ? descriptionElement.attr("content")
                : "";

        return new UrlCheck(statusCode, title, h1, description, url.getId());
    }
}
