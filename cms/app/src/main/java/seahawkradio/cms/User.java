package seahawkradio.cms;

import java.util.UUID;

public class User {
    public final UUID id;
    public final String username;

    // The user's email as it was entered on registration
    public final String email;

    // The user's email converted to lowercase
    public final String emailNormalized;

    // base64-encoded hashed password
    public final String password;

    public User(UUID id, String username, String email, String emailNormalized, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.emailNormalized = emailNormalized;
        this.password = password;
    }

    @Override
    public int hashCode() {
        // really not sure if this is a good way to override hashCode
        // in theory it could just be id I guess
        return (id.toString() + username + email + emailNormalized + password).hashCode();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (!(otherObj instanceof User)) {
            return false;
        }

        User other = (User) otherObj;
        return this.id.equals(other.id) && this.username.contentEquals(other.username)
                && this.email.contentEquals(other.email)
                && this.emailNormalized.contentEquals(other.emailNormalized)
                && this.password.contentEquals(other.password);
    }

    @Override
    public String toString() {
        return String.format(
                "User(id=\"%s\", username=\"%s\", email=\"%s\", emailNormalized=\"%s\", password=\"%s\")",
                id, username, email, emailNormalized, password);
    }
}
