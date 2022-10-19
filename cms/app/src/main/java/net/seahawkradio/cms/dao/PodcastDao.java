package net.seahawkradio.cms.dao;

import net.seahawkradio.cms.models.Identity;
import net.seahawkradio.cms.models.PodcastMetadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PodcastDao {
    private static final Logger LOG = LoggerFactory.getLogger(MediaDao.class);
    Connection conn;

    public PodcastDao(Connection conn) {
        this.conn = conn;
    }

    public PodcastMetadata create(
            String title,
            String description,
            String author,
            String copyright,
            Boolean explicit,
            Identity owner,
            Set<String> categories) {
        final var created = OffsetDateTime.now();
        final var link = ""; // TODO: generate permanent links for shows
        final var podcast =
                new PodcastMetadata(
                        UUID.randomUUID(),
                        title,
                        link,
                        description,
                        copyright,
                        Optional.empty(),
                        copyright,
                        explicit,
                        categories,
                        author,
                        owner,
                        created,
                        created,
                        Optional.empty());

        final String podcastInsertStatementText =
                """
                INSERT INTO podcast (id, title, link, description, language, author, copyright, pub_date, explicit, owner_name, owner_email, created, updated)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """;
        final String categoryInsertStatementText =
                """
                INSERT INTO podcast_category (podcast_id, category) VALUES (?, ?)
                """;
        final var podcastIdStr = podcast.id().toString();
        try (final var podcastInsertStatement =
                this.conn.prepareStatement(podcastInsertStatementText)) {

            // Its fine if this value is null, the pub_date column is nullable
            @SuppressWarnings("java:S3655")
            final String pubDateStr =
                    podcast.pubDate()
                            .map(d -> d.format(DateTimeFormatter.RFC_1123_DATE_TIME))
                            .orElse(null);

            podcastInsertStatement.setString(1, podcastIdStr);
            podcastInsertStatement.setString(2, podcast.title());
            podcastInsertStatement.setString(3, podcast.link());
            podcastInsertStatement.setString(4, podcast.description());
            podcastInsertStatement.setString(5, podcast.language());
            podcastInsertStatement.setString(6, podcast.author());
            podcastInsertStatement.setString(7, podcast.copyright());
            podcastInsertStatement.setString(8, pubDateStr);
            podcastInsertStatement.setBoolean(9, podcast.explicit());
            podcastInsertStatement.setString(10, podcast.owner().name());
            podcastInsertStatement.setString(11, podcast.owner().email());
            podcastInsertStatement.setString(12, podcast.created().toString());
            podcastInsertStatement.setString(13, podcast.updated().toString());

            podcastInsertStatement.executeUpdate();
        } catch (SQLException e) {
            LOG.atError()
                    .setCause(e)
                    .addKeyValue("title", podcast.title())
                    .addKeyValue("id", podcast.id())
                    .log("failed to insert podcast record into database");
        }
        if (!categories.isEmpty()) {
            try (final var podcastCategoryInsertStatement =
                    this.conn.prepareStatement(categoryInsertStatementText)) {
                for (var category : categories) {
                    podcastCategoryInsertStatement.setString(1, podcastIdStr);
                    podcastCategoryInsertStatement.setString(2, category);
                    podcastCategoryInsertStatement.addBatch();
                }
                podcastCategoryInsertStatement.executeBatch();
            } catch (SQLException e) {
                LOG.atError()
                        .setCause(e)
                        .addKeyValue("title", podcast.title())
                        .addKeyValue("id", podcast.id())
                        .log("failed to insert podcast categories into database");
            }
        }

        return podcast;
    }
}
