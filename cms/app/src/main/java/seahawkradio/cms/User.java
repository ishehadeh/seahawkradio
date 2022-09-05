package seahawkradio.cms;

import java.util.UUID;

public class User {
    public final UUID id;
    public final String username;
    public final String email;

    // base64-encoded hashed password
    public final String password;

    public User(UUID id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
