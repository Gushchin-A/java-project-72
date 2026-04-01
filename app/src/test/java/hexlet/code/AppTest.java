package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.TextTruncate;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import mockwebserver3.MockResponse;
import mockwebserver3.MockWebServer;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    /** Test status variable. */
    private static final int STATUS_200 = 200;
    /** Test status variable. */
    private static final int STATUS_422 = 422;
    /** Test status variable. */
    private static final int STATUS_404 = 404;
    /** Test status variable. */
    private static final String STATUS_200_STR = "200";
    /** Test html success page. */
    private static final String SUCCESS_PAGE = "success-page.html";
    /** Test html long text. */
    private static final String LONG_TEXT = "long-string.html";

    /** Mock server. */
    private static MockWebServer mockWebServer;

    /** App variable for tests. */
    private Javalin app;

    @BeforeAll
    static void startMockServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockServer() throws IOException {
        mockWebServer.close();
    }

    @BeforeEach
    void setUp() throws IOException, SQLException {
        app = App.getApp();
        clearTables();
    }

    @Test
    void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());
            assertTrue(response.body().string().contains("Анализатор страниц"));
        });
    }

    @Test
    void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/urls");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());
            assertTrue(response.body().string().contains("Сайты"));
        });
    }

    @Test
    void testShowUrlPage() throws SQLException {
        Url url = new Url("https://www.starwars.com");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/urls/" + url.getId());

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());
            assertTrue(response
                    .body().string().contains("https://www.starwars.com"));
        });
    }

    @Test
    void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.starwars.com";
            Response postResponse = client.post("/urls", requestBody);

            assertEquals(STATUS_200, postResponse.code());

            Url savedUrl = UrlRepository
                    .findByName("https://www.starwars.com").orElseThrow();
            Response getResponse = client.get("/urls/" + savedUrl.getId());

            assertEquals(STATUS_200, getResponse.code());
            Assertions.assertNotNull(getResponse.body());
            assertTrue(getResponse
                    .body().string().contains("https://www.starwars.com"));
        });
    }

    @Test
    void testCreateUrlDuplicate() throws SQLException {
        Url url = new Url("https://www.starwars.com");
        UrlRepository.save(url);
        int urlsBefore = UrlRepository.getEntities().size();

        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=https://www.starwars.com";
            Response postResponse = client.post("/urls", requestBody);

            assertEquals(STATUS_200, postResponse.code());
            Assertions.assertNotNull(postResponse.body());
            assertTrue(postResponse
                    .body().string().contains("https://www.starwars.com"));
        });

        int urlsAfter = UrlRepository.getEntities().size();

        assertEquals(urlsBefore, urlsAfter);
    }

    @Test
    void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            String requestBody = "url=w w w .leningrad.spb.tochka.ru";
            Response postResponse = client.post("/urls", requestBody);

            assertEquals(STATUS_422, postResponse.code());
            Assertions.assertNotNull(postResponse.body());
            assertTrue(postResponse
                    .body().string().contains("Некорректный URL"));
        });
    }

    @Test
    void testShowNotExistUrlPage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/urls/" + "19121");

            assertEquals(STATUS_404, response.code());
            Assertions.assertNotNull(response.body());
            assertTrue(response.body().string().contains("Url not found"));
        });
    }

    @Test
    void testGetAllUrlsPages() throws SQLException {
        Url url1 = new Url("https://www.starwars.com");
        Url url2 = new Url("https://www.gosuslugi.com");

        UrlRepository.save(url1);
        UrlRepository.save(url2);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/urls");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());

            String responseBody = response.body().string();

            assertTrue(responseBody.contains("https://www.starwars.com"));
            assertTrue(responseBody.contains("https://www.gosuslugi.com"));
        });
    }

    @Test
    void testCreateUrlCheck() throws IOException, SQLException {
        String html = readFixture(SUCCESS_PAGE);

        mockWebServer.enqueue(new MockResponse.Builder()
                .code(STATUS_200)
                .body(html)
                .build());

        Url url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.post(
                    "/urls/" + url.getId() + "/checks", "");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());

            String body = response.body().string();

            assertTrue(body.contains("Star Wars page"));
            assertTrue(body.contains("May the Force be with you"));
            assertTrue(body.contains("Let's celebrate on May 4th!"));
        });
    }

    @Test
    void testCreateUrlCheckWithError() throws SQLException {
        mockWebServer.enqueue(new MockResponse.Builder()
                .code(STATUS_404)
                .body("")
                .build());

        Url url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.post(
                    "/urls/" + url.getId() + "/checks", "");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());

            List<UrlCheck> checks = UrlCheckRepository.findByUrlId(url.getId());
            assertTrue(checks.isEmpty());
        });
    }

    @Test
    void testUrlsPageShowsLastCheck() throws SQLException {
        Url url = new Url("https://www.starwars.com");
        UrlRepository.save(url);

        UrlCheck check = new UrlCheck(
                STATUS_200,
                "Star Wars page",
                "May the Force be with you",
                "Let's celebrate on May 4th!",
                url.getId()
        );

        UrlCheckRepository.save(check);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.get("/urls");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());

            String body = response.body().string();

            assertTrue(body.contains("https://www.starwars.com"));
            assertTrue(body.contains(STATUS_200_STR));
        });
    }

    @Test
    void testCreateUrlCheckWithTruncatedFields()
            throws IOException, SQLException {
        String html = readFixture(LONG_TEXT);

        mockWebServer.enqueue(new MockResponse.Builder()
                .code(STATUS_200)
                .body(html)
                .build());

        Url url = new Url(mockWebServer.url("/").toString());
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            Response response = client.post(
                    "/urls/" + url.getId() + "/checks", "");

            assertEquals(STATUS_200, response.code());
            Assertions.assertNotNull(response.body());

            String body = response.body().string();

            Document document = Jsoup.parse(html);

            String expectedTitle = TextTruncate.truncate(document.title());
            String expectedDescription = TextTruncate.truncate(
                    Objects.requireNonNull(document.selectFirst(
                                    "meta[name=description]"))
                            .attr("content"));
            String expectedH1 = TextTruncate.truncate(
                    Objects.requireNonNull(document.selectFirst("h1")).text()
            );

            assertTrue(body.contains(expectedTitle));
            assertTrue(body.contains(expectedDescription));
            assertTrue(body.contains(expectedH1));
        });
    }

    private void clearTables() throws SQLException {
        try (Connection connection = BaseRepository
                .getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("SET REFERENTIAL_INTEGRITY FALSE");
            statement.execute("TRUNCATE TABLE url_checks RESTART IDENTITY");
            statement.execute("TRUNCATE TABLE urls RESTART IDENTITY");
            statement.execute("SET REFERENTIAL_INTEGRITY TRUE");
        }
    }

    private String readFixture(final String fileName) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("fixtures/" + fileName)) {
            if (inputStream == null) {
                throw new IOException("Fixture not found: " + fileName);
            }
            return new String(
                    inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
