package seahawkradio.cms;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public record User(UUID id, String username, String email, String emailNormalized, String password,
        ZonedDateTime created, ZonedDateTime updated, Optional<ZonedDateTime> deleted) {
}
