package hexlet.code.repository;

import com.zaxxer.hikari.HikariDataSource;

public final class BaseRepository {
    /** Shared datasource for repositories. */
    private static HikariDataSource dataSource;

    private BaseRepository() {
    }

    /**
     * Sets datasource for repositories.
     *
     * @param newDataSource datasource
     */
    public static void setDataSource(final HikariDataSource newDataSource) {
        dataSource = newDataSource;
    }

    /**
     * Gets repository datasource.
     *
     * @return datasource
     */
    public static HikariDataSource getDataSource() {
        return dataSource;
    }
}
