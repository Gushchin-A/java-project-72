package hexlet.code.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

/** Url entity. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Url {
    /** Url identifier. */
    private Long id;
    /** Url name. */
    private String name;
    /** Url timestamp. */
    private Timestamp createdAt;

    /**
     * Creates url with name only.
     *
     * @param urlName url name
     */
    public Url(final String urlName) {
        this.name = urlName;
    }
}
