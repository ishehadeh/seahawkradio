package seahawkradio.cms;

import java.util.UUID;

public class User {
    public final UUID id;
    public final String username;
    public final String email;
    public final byte[] password;

    public User(UUID id, String username, String email, byte[] password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
