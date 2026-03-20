package hexlet.code;

import io.javalin.Javalin;

public final class App {

    private App() {
    }

    /**
     * Builds Javalin application.
     *
     * @return Javalin app
     */
    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        app.get("/", ctx -> ctx.result("Hello, world!"));

        return app;
    }

    /**
     * Starts the app.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        Javalin app = getApp();
        int port = getPort();
        app.start(port);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");

        return Integer.parseInt(port);
    }
}
