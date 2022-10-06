package net.seahawkradio.cms;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Session(UUID id, UUID userId, OffsetDateTime created, OffsetDateTime expires) {}
