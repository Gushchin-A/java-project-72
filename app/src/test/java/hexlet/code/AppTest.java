package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {
    /** Test status variable. */
    private static final int STATUS_200 = 200;
    /** Test status variable. */
    private static final int STATUS_422 = 422;
    /** Test status variable. */
    private static final int STATUS_404 = 404;

    /** App variable for tests. */
    private Javalin app;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        app = App.getApp();
        clearUrlsTable();
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

    private void clearUrlsTable() throws SQLException {
        try (Connection connection = BaseRepository
                .getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE urls RESTART IDENTITY");
        }
    }
}
