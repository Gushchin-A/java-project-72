package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Repository for url entities. */
public final class UrlRepository {

    private UrlRepository() {
    }

    /**
     * Saves url to database.
     *
     * @param url url entity
     * @throws SQLException if database error
     */
    public static void save(final Url url) throws SQLException {
        String sql = "INSERT INTO urls (name) VALUES (?)";

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, url.getName());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Finds url by id.
     *
     * @param id url identifier
     * @throws SQLException if database error
     * @return optional with url if found
     */
    public static Optional<Url> find(final Long id) throws SQLException {
        String sql = "SELECT * FROM urls WHERE id = ?";

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Url url = buildUrl(resultSet);
                    return Optional.of(url);
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Finds url by name.
     *
     * @param name url
     * @throws SQLException if database error
     * @return optional with url if found
     */
    public static Optional<Url> findByName(
            final String name) throws SQLException {
        String sql = "SELECT * FROM urls WHERE name = ?";

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Url url = buildUrl(resultSet);
                    return Optional.of(url);
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Returns all urls.
     *
     * @return list of urls
     * @throws SQLException if database error
     */
    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT * FROM urls ORDER BY created_at DESC";

        try (Connection connection = BaseRepository
                .getDataSource()
                .getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            List<Url> urls = new ArrayList<>();

            while (resultSet.next()) {
                urls.add(buildUrl(resultSet));
            }

            return urls;
        }
    }

    private static Url buildUrl(final ResultSet resultSet) throws SQLException {
        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at").toInstant());
    }
}
