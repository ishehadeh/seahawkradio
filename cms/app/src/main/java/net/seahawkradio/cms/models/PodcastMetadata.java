package net.seahawkradio.cms.models;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
// NOTE: see https://podcasters.apple.com/support/1691-apple-podcasts-categories for a list of
// itunes categories

public record PodcastMetadata(
        UUID id,
        String title,
        String link,
        String description,
        String copyright,
        Optional<ZonedDateTime> pubDate,
        String language,
        boolean explicit,
        Set<String> itunesCategories,
        String author,
        Identity owner,
        OffsetDateTime created,
        OffsetDateTime updated,
        Optional<OffsetDateTime> deleted) {}
