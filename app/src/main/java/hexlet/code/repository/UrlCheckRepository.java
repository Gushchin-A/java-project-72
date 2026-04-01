package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Repository for url check entities. */
public final class UrlCheckRepository {

    private UrlCheckRepository() {
    }

    /**
     * Saves url check to database.
     *
     * @param urlCheck url check entity
     * @throws SQLException if database error
     */
    public static void save(final UrlCheck urlCheck) throws SQLException {
        String sql = """
                INSERT INTO url_checks (url_id, status_code, title, h1, description)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setLong(1, urlCheck.getUrlId());
            statement.setInt(2, urlCheck.getStatusCode());
            statement.setString(3, urlCheck.getTitle());
            statement.setString(4, urlCheck.getH1());
            statement.setString(5, urlCheck.getDescription());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    urlCheck.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Finds all checks for url by id.
     *
     * @param urlId url identifier
     * @throws SQLException if database error
     * @return list url checks
     */
    public static List<UrlCheck> findByUrlId(final Long urlId) throws SQLException {
        String sql = "SELECT * FROM url_checks WHERE url_id = ? ORDER BY id DESC";

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, urlId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<UrlCheck> checks = new ArrayList<>();
                while (resultSet.next()) {
                    checks.add(buildUrlCheck(resultSet));
                }
                return checks;
            }
        }
    }

    /**
     * Returns latest checks for urls.
     *
     * @return map key=urlId, value=latest check
     * @throws SQLException if database error
     */
    public static Map<Long, UrlCheck> getLastChecks() throws SQLException {
        String sql = """
            SELECT url_checks.*
            FROM url_checks
            JOIN (
                SELECT url_id, MAX(id) as max_id
                FROM url_checks
                GROUP BY url_id
            ) latest_checks
            ON url_checks.id = latest_checks.max_id
            """;

        try (Connection connection = BaseRepository.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            Map<Long, UrlCheck> checks = new HashMap<>();

            while (resultSet.next()) {
                UrlCheck check = buildUrlCheck(resultSet);
                checks.put(check.getUrlId(), check);
            }

            return checks;
        }
    }

    private static UrlCheck buildUrlCheck(final ResultSet resultSet) throws SQLException {
        return new UrlCheck(
                resultSet.getLong("id"),
                resultSet.getInt("status_code"),
                resultSet.getString("title"),
                resultSet.getString("h1"),
                resultSet.getString("description"),
                resultSet.getLong("url_id"),
                resultSet.getTimestamp("created_at")
        );
    }
}
