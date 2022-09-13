/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package seahawkradio.cms;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import seahawkradio.cms.PodcastEpisode.Enclosure;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);


    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        final String databaseURL = "jdbc:sqlite:";

        LOG.info("initializing Seahawk Radio CSM");

        String schema = null;
        LOG.info("opening database schema resource");
        try (var schemaFile = App.class.getResource("/sql/schema.sql").openStream()) {
            // TODO instead of a single schema we should apply a series of migrations
            schema = new String(schemaFile.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error("failed to read SQL schema", e);
            System.exit(1);
        }

        // Supress resource not closed lint
        // it isn't necessary to explicitly call close on Javalin apps.
        @SuppressWarnings("java:S2095")
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("static", Location.CLASSPATH);
        });

        Connection databaseConnection = null;
        try {
            LOG.info("opening database connection '{}'", databaseURL);

            // AFAIK there's no reason to pool SQLite connections.
            // They're also threadsafe so we can just create one for the lifetime of this
            // application
            databaseConnection = DriverManager.getConnection(databaseURL);
        } catch (SQLException e) {
            LOG.error("failed to open database '{}'", databaseURL, e);
            System.exit(1);
        }

        LOG.info("applying database schema");
        try (var stmt = databaseConnection.createStatement()) {
            for (var stmtStr : schema.split(";")) {
                stmt.executeUpdate(stmtStr);
            }
        } catch (SQLException e) {
            LOG.error("failed to apply schema", e);
            System.exit(1);
        }

        try {
            new UserDao(databaseConnection).create("admin", "admin@example.com", "password123");
        } catch (SQLException e) {
            LOG.error("failed to create admin user", e);
            System.exit(1);
        }
        final PodcastMetadata podcast = new PodcastMetadata(UUID.randomUUID(), "Test",
                "http://localhost:8080/test", "Test podcast feed.", "Example Copyright",
                ZonedDateTime.now(), "en-us", false, Set.of("Film Reviews", "Fantasy Sports"),
                "Seahawk Radio", new Identity("admin@example.com", "The Admin"));
        final PodcastEpisode[] eps = new PodcastEpisode[] {new PodcastEpisode(UUID.randomUUID(),
                "Test Ep 1", "This is a test episode", Duration.ofMinutes(10),
                new Enclosure("http://example.com/example.mp3", "audio/mpeg", 498537), true,
                ZonedDateTime.now())};
        app.attribute("database", databaseConnection);
        app.get("/", ctx -> ctx.render("index.jte"));
        app.get("/test.rss",
                ctx -> ctx.render("feed.rss.jte", Map.of("podcast", podcast, "episodes", eps))
                        .contentType("application/rss+xml"));
        app.post("/login", UserController.login);
        app.start(8080);
    }
}
