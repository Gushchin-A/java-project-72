package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.UrlChecker;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Controller for URL pages and URL checks. */
public final class UrlController {

    private UrlController() {
    }

    /**
     * Renders the list of URLs.
     *
     * @param ctx Javalin request context
     * @throws SQLException if database query fails
     */
    public static void index(Context ctx) throws SQLException {
        List<Url> urls = UrlRepository.getEntities();
        String flash = ctx.consumeSessionAttribute("flash");

        Map<String, Object> model = new HashMap<>();
        model.put("urls", urls);
        model.put("flash", flash);

        Map<Long, UrlCheck> lastChecks = UrlCheckRepository.getLastChecks();
        model.put("lastChecks", lastChecks);

        ctx.render("urls/index.jte", model);
    }

    /**
     * Renders url page with checks.
     *
     * @param ctx Javalin request context
     * @throws SQLException if database query fails
     */
    public static void show(Context ctx) throws SQLException {
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
    }

    /**
     * Creates a new url.
     *
     * @param ctx Javalin request context
     */
    public static void create(Context ctx)
            throws IllegalStateException, SQLException {
        String input = ctx.formParam("url");
        String normalizedUrl;

        try {
            normalizedUrl = normalizeUrl(input);
        } catch (Exception e) {
            ctx.status(HttpStatus.UNPROCESSABLE_CONTENT);
            Map<String, String> model = new HashMap<>();
            model.put("flash", "Некорректный URL");
            ctx.render("index.jte", model);
            return;
        }

        Optional<Url> existingUrl = UrlRepository.findByName(normalizedUrl);

        if (existingUrl.isPresent()) {
            Url url = existingUrl
                    .orElseThrow(() -> new IllegalStateException(
                            "Url must exist"));
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.redirect("/urls/" + url.getId());
            return;
        }

        Url url = new Url(normalizedUrl);
        UrlRepository.save(url);
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.redirect("/urls/" + url.getId());
    }

    public static void check(Context ctx)
            throws NotFoundResponse, SQLException {
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
}
