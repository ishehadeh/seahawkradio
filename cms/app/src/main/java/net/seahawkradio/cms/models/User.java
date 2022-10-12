package net.seahawkradio.cms.models;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public record User(
        UUID id,
        String username,
        String email,
        String emailNormalized,
        String password,
        OffsetDateTime created,
        OffsetDateTime updated,
        Optional<OffsetDateTime> deleted) {}
