package seahawkradio.cms;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

public class UserDao {
    private static final Logger LOG = LoggerFactory.getLogger(UserDao.class);
    private static final Argon2 argon2 =
            Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2i, 32, 64);
    Connection conn;

    UserDao(Connection conn) {
        this.conn = conn;
    }

    // Get User from a query result row. Row must be id, username, email, email_normalized, password
    protected static User userFromRow(ResultSet row) throws SQLException {
        return new User(UUID.fromString(row.getString(1)), row.getString(2), row.getString(3),
                row.getString(4), row.getString(5),
                ZonedDateTime.parse(row.getString(6), DateTimeFormatter.ISO_DATE_TIME),
                ZonedDateTime.parse(row.getString(7), DateTimeFormatter.ISO_DATE_TIME),
                Optional.ofNullable(row.getString(8))
                        .map(x -> ZonedDateTime.parse(x, DateTimeFormatter.ISO_DATE_TIME)));
    }

    // Convert an email address to its canonical form.
    // useful when email addresses need to be compared.
    // e.g. JohnDoe@example.com -> johndoe@example.com
    protected static String normalizeEmail(String email) {
        // in the future this could apply other transformations, like stripping comments.
        return email.toLowerCase();
    }

    User create(String username, String email, String password) throws SQLException {
        final byte[] utf8Password = password.getBytes(StandardCharsets.UTF_8);
        final String hashedPassword = argon2.hash(22, 65536, 1, utf8Password);
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        User user = new User(UUID.randomUUID(), username, email, normalizeEmail(email),
                hashedPassword, now, now, Optional.empty());

        final String update =
                "INSERT INTO USERS (id, username, email, email_normalized, password, created, updated) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (var statement = this.conn.prepareStatement(update)) {
            statement.setString(1, user.id.toString());
            statement.setString(2, user.username);
            statement.setString(3, user.email);
            statement.setString(4, user.emailNormalized);
            statement.setString(5, user.password);
            statement.setString(6, user.created.format(DateTimeFormatter.ISO_DATE_TIME));
            statement.setString(7, user.updated.format(DateTimeFormatter.ISO_DATE_TIME));
            statement.executeUpdate();
        }
        return user;
    }

    Optional<User> login(String email, String password) throws SQLException {
        final byte[] utf8Password = password.getBytes(StandardCharsets.UTF_8);
        final String query =
                "SELECT id, username, email, email_normalized, password, created, updated, deleted FROM users WHERE email_normalized = ?";
        try (var statement = this.conn.prepareStatement(query)) {
            statement.setString(1, normalizeEmail(email));
            var rows = statement.executeQuery();
            if (!rows.next()) {
                LOG.info("no user with email '{}'", email);
                return Optional.empty();
            }
            User user = userFromRow(rows);
            if (!argon2.verify(user.password, utf8Password)) {
                LOG.info("password did not match for '{}'", email);
                return Optional.empty();
            }

            return Optional.of(user);
        }
    }


}
