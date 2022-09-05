package seahawkradio.cms;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import java.util.Optional;

public class UserDaoTest {

    static Connection openDatabase() throws IOException, SQLException {
        String schema = null;
        try (var schemaFile = App.class.getResource("/sql/schema.sql").openStream()) {
            schema = new String(schemaFile.readAllBytes(), StandardCharsets.UTF_8);
        }
        var databaseConnection = DriverManager.getConnection("jdbc:sqlite:");
        try (var stmt = databaseConnection.createStatement()) {
            stmt.addBatch(schema);
            stmt.executeBatch();
        }

        return databaseConnection;
    }

    @Test
    void createUser() throws IOException, SQLException {
        var db = openDatabase();
        var users = new UserDao(db);
        var user1 = users.create("test1", "test1@example.com", "password1");
        var user2 = users.create("test2", "test2@example.com", "password2");
        var user3 = users.create("test3", "test3@example.com", "password3");

        final String query = "SELECT id, username, email, password FROM users";
        try (var statement = db.prepareStatement(query)) {
            var rows = statement.executeQuery();
            assertTrue(rows.next());
            assertEquals(UserDao.userFromRow(rows), user1);
            assertTrue(rows.next());
            assertEquals(UserDao.userFromRow(rows), user2);
            assertTrue(rows.next());
            assertEquals(UserDao.userFromRow(rows), user3);
        }
    }

    @Test
    void loginUser() throws IOException, SQLException {
        var users = new UserDao(openDatabase());
        var user = users.create("test1", "test1@example.com", "password1");
        assertEquals(users.login("test1@example.com", "password1").get(), user);
    }
}
