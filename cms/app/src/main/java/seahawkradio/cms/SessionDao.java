package seahawkradio.cms;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionDao {
    private static final Logger LOG = LoggerFactory.getLogger(SessionDao.class);

    private Connection conn;

    public SessionDao(Connection conn) {
        this.conn = conn;
    }

    // Get Session from a query result row. Row must be id, user_id, created, expires
    protected static Session sessionFromRow(ResultSet row) throws SQLException {
        return new Session(UUID.fromString(row.getString(1)), UUID.fromString(row.getString(2)),
                OffsetDateTime.parse(row.getString(3)), OffsetDateTime.parse(row.getString(4)));
    }

    public Session create(User user, Duration duration) throws SQLException {
        final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        Session session = new Session(UUID.randomUUID(), user.id(), now, now.plus(duration));
        LOG.debug("creating session: {}", session);
        final String update =
                "INSERT INTO sessions (id, user_id, created, expires) VALUES (?, ?, ?, ?)";
        try (var statement = this.conn.prepareStatement(update)) {
            statement.setString(1, session.id().toString());
            statement.setString(2, user.id().toString());
            statement.setString(3, session.created().toString());
            statement.setString(4, session.expires().toString());
            statement.executeUpdate();
        }

        return session;
    }
}
