package net.seahawkradio.cms;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

public class SessionDaoTest {
    @Test
    void createSession() throws IOException, SQLException {
        var db = DbUtil.openDatabase();
        var users = new UserDao(db);
        var sessions = new SessionDao(db);
        var user = users.create("sessionTest", "sessionTest@example.com", "password123");
        var session = sessions.create(user, Duration.ofDays(7));
        final String query = "SELECT id, user_id, created, expires FROM sessions";
        try (var statement = db.prepareStatement(query)) {
            var rows = statement.executeQuery();
            assertTrue(rows.next());
            assertEquals(SessionDao.sessionFromRow(rows), session);
        }
    }

    @Test
    void getSession() throws IOException, SQLException {
        var db = DbUtil.openDatabase();
        var users = new UserDao(db);
        var sessions = new SessionDao(db);
        var user = users.create("sessionTest", "sessionTest@example.com", "password123");
        var session = sessions.create(user, Duration.ofDays(7));
        var session2 = sessions.create(user, Duration.ofSeconds(0));
        assertEquals(sessions.get(session.id()).get(), session);
        assertTrue(sessions.get(session2.id()).isEmpty());
    }
}
