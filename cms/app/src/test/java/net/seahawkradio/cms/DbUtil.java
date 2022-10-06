package net.seahawkradio.cms;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil {
    static Connection openDatabase() throws IOException, SQLException {
        String schema = null;
        try (var schemaFile = App.class.getResource("/sql/schema.sql").openStream()) {
            schema = new String(schemaFile.readAllBytes(), StandardCharsets.UTF_8);
        }

        // leaving off the sqlite database name creates a new in-memory database
        var databaseConnection = DriverManager.getConnection("jdbc:sqlite:");
        try (var stmt = databaseConnection.createStatement()) {
            for (var stmtStr : schema.split(";")) {
                stmt.executeUpdate(stmtStr);
            }
        }

        return databaseConnection;
    }
}
