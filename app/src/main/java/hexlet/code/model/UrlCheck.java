package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Url check entity. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UrlCheck {
    /** UrlCheck identifier. */
    private Long id;
    /** Status code. */
    private Integer statusCode;
    /** Page title. */
    private String title;
    /** First heading. */
    private String h1;
    /** Description. */
    private String description;
    /** Relation Url ID. */
    private long urlId;
    /** UrlCheck creation time. */
    private Instant createdAt;

    /**
     * Creates url check.
     *
     * @param checkStatusCode response status code
     * @param checkTitle page title
     * @param checkH1 first heading
     * @param checkDescription meta description
     * @param checkUrlId related url id
     */
    public UrlCheck(
            final Integer checkStatusCode,
            final String checkTitle,
            final String checkH1,
            final String checkDescription,
            final long checkUrlId
    ) {
        this.statusCode = checkStatusCode;
        this.title = checkTitle;
        this.h1 = checkH1;
        this.description = checkDescription;
        this.urlId = checkUrlId;
    }
}
