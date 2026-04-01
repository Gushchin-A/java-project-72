package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.UrlChecker;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class App {
    /** Unprocessable Entity HTTP status. */
    private static final int UNPROCESSABLE_ENTITY_STATUS = 422;

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
                config -> {
                    config.bundledPlugins.enableDevLogging();
                    config.fileRenderer(new JavalinJte(createTemplateEngine()));
                });

        app.get("/", ctx -> {
            String flash = ctx.consumeSessionAttribute("flash");
            Map<String, Object> model = new HashMap<>();
            model.put("flash", flash);
            ctx.render("index.jte", model);
        });

        app.get("/urls", ctx -> {
            List<Url> urls = UrlRepository.getEntities();
            String flash = ctx.consumeSessionAttribute("flash");

            Map<String, Object> model = new HashMap<>();
            model.put("urls", urls);
            model.put("flash", flash);

            Map<Long, UrlCheck> lastChecks = UrlCheckRepository.getLastChecks();
            model.put("lastChecks", lastChecks);

            ctx.render("urls/index.jte", model);
        });

        app.get("/urls/{id}", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Url not found"));

            String flash = ctx.consumeSessionAttribute("flash");
            Map<String, Object> model = new HashMap<>();
            model.put("url", url);
            model.put("flash", flash);

            List<UrlCheck> checks = UrlCheckRepository.findByUrlId(id);
            model.put("checks", checks);

            ctx.render("urls/show.jte", model);
        });

        app.post("/urls", ctx -> {
            String input = ctx.formParam("url");
            try {
                String normalizedUrl = normalizeUrl(input);
                Optional<Url> existingUrl = UrlRepository
                        .findByName(normalizedUrl);

                if (existingUrl.isPresent()) {
                    ctx.sessionAttribute("flash", "Страница уже существует");
                    ctx.redirect("/urls/" + existingUrl.get().getId());
                    return;
                }

                Url url = new Url(normalizedUrl);
                UrlRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.redirect("/urls/" + url.getId());

            } catch (Exception e) {
                ctx.status(UNPROCESSABLE_ENTITY_STATUS);
                Map<String, String> model = new HashMap<>();
                model.put("flash", "Некорректный URL");
                ctx.render("index.jte", model);
            }
        });

        app.post("/urls/{id}/checks", ctx -> {
            Long id = ctx.pathParamAsClass("id", Long.class).get();
            Url url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Url not found"));

            try {
                UrlCheck urlCheck = UrlChecker.check(url);
                UrlCheckRepository.save(urlCheck);
                ctx.sessionAttribute("flash", "Страница успешно проверена");
            } catch (RuntimeException e) {
                ctx.sessionAttribute("flash", "Произошла ошибка при проверке");
            }

            ctx.redirect("/urls/" + id);
        });

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

    private static String normalizeUrl(final String rawUrl) throws Exception {
        URI uri = new URI(rawUrl);
        URL url = uri.toURL();

        String protocol = url.getProtocol();
        String host = url.getHost();
        int port = url.getPort();

        if (port == -1) {
            return protocol + "://" + host;
        }

        return protocol + "://" + host + ":" + port;
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

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver(
                "templates", classLoader);

        return TemplateEngine.create(codeResolver, ContentType.Html);
    }
}
