package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class App {

    private App() {
    }

    /**
     * Builds Javalin application.
     *
     * @throws IOException  if schema file cannot be read
     * @throws SQLException if database init fails
     * @return Javalin app
     */
    public static Javalin getApp() throws IOException, SQLException {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDatabaseUrl());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);
        BaseRepository.setDataSource(dataSource);

        initDatabase();

        Javalin app = Javalin.create(
                config -> config.bundledPlugins.enableDevLogging());

        app.get("/", ctx -> ctx.result("Hello, world!"));

        return app;
    }

    /**
     * Starts the app.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args)
            throws SQLException, IOException {
        Javalin app = getApp();
        int port = getPort();
        app.start(port);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.parseInt(port);
    }

    private static String getDatabaseUrl() {
        return System.getenv().getOrDefault(
                "JDBC_DATABASE_URL",
                "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;"
        );
    }

    private static void initDatabase()
            throws IOException, SQLException {
        try (InputStream inputStream = App.class.getClassLoader()
                .getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                throw new IOException("schema.sql not found");
            }

            String schema = new String(
                    inputStream.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection connection = BaseRepository
                    .getDataSource()
                    .getConnection();
                 Statement statement = connection.createStatement()) {
                statement.execute(schema);
            }
        }
    }
}
