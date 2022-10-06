package net.seahawkradio.cms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

public class MediaDao {
    private static final Logger LOG = LoggerFactory.getLogger(MediaDao.class);
    Connection conn;

    MediaDao(Connection conn) {
        this.conn = conn;
    }

    public Optional<Media> byId(UUID id) throws SQLException {
        LOG.atDebug().addKeyValue("id", id).log("fetching media from database");
        final var statementText =
                "SELECT filename, content_type, created, deleted FROM media WHERE id = ?";
        try (var statement = this.conn.prepareStatement(statementText)) {
            statement.setString(1, id.toString());
            final var rows = statement.executeQuery();
            if (!rows.next()) {
                return Optional.empty();
            }

            return Optional.of(
                    new Media(
                            id,
                            rows.getString(1),
                            rows.getString(2),
                            OffsetDateTime.parse(rows.getString(3)),
                            Optional.ofNullable(rows.getString(4)).map(OffsetDateTime::parse)));
        }
    }

    public Media create(String filename, String contentType) throws SQLException {
        final var media =
                new Media(
                        UUID.randomUUID(),
                        filename,
                        contentType,
                        OffsetDateTime.now(ZoneOffset.UTC),
                        Optional.empty());
        LOG.atDebug()
                .addKeyValue("filename", filename)
                .addKeyValue("contentType", contentType)
                .addKeyValue("id", media.id())
                .log("adding media entry to database");
        final var statementText =
                "INSERT INTO media (id, filename, content_type, created) VALUES (?, ?, ?, ?);";
        try (var statement = this.conn.prepareStatement(statementText)) {
            statement.setString(1, media.id().toString());
            statement.setString(2, media.filename());
            statement.setString(3, media.contentType());
            statement.setString(4, media.created().toString());
            statement.executeUpdate();
        }
        return media;
    }
}
