package seahawkradio.cms;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    // Get User from a query result row. Row must be id, username, email, password
    protected static User userFromRow(ResultSet row) throws SQLException {
        return new User(UUID.fromString(row.getString(1)), row.getString(2), row.getString(3),
                row.getString(4));
    }

    User create(String username, String email, String password) throws SQLException {
        // TODO normalize email
        final byte[] utf8Password = password.getBytes(StandardCharsets.UTF_8);
        final String hashedPassword = argon2.hash(22, 65536, 1, utf8Password);
        User user = new User(UUID.randomUUID(), username, email, hashedPassword);

        final String update =
                "INSERT INTO USERS (id, username, email, password) VALUES (?, ?, ?, ?)";
        try (var statement = this.conn.prepareStatement(update)) {
            statement.setString(1, user.id.toString());
            statement.setString(2, user.username);
            statement.setString(3, user.email);
            statement.setString(4, user.password);
            statement.executeUpdate();
        }
        return user;
    }

    Optional<User> login(String email, String password) throws SQLException {
        final byte[] utf8Password = password.getBytes(StandardCharsets.UTF_8);
        final String query = "SELECT id, username, email, password FROM users WHERE email = ?";
        try (var statement = this.conn.prepareStatement(query)) {
            statement.setString(1, email);
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
