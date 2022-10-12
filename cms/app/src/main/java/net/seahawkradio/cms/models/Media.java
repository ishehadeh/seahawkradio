package net.seahawkradio.cms.models;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public record Media(
        UUID id,
        String filename,
        String contentType,
        OffsetDateTime created,
        Optional<OffsetDateTime> deleted) {}
