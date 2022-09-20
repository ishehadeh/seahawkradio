package seahawkradio.cms;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record PodcastEpisode(
        UUID id,
        String title,
        String description,
        Duration duration,
        List<Enclosure> enclosures,
        boolean explicit,
        ZonedDateTime pubDate) {

    public PodcastEpisode {
        // List.copyOf returns an immutable copy.
        // We create an immutable copy of enclosures to ensure it can't be modified
        // after constructing the record, just like the other fields.
        // Otherwise `podcastEpisode.enclosures().add(...)` would work.
        enclosures = List.copyOf(enclosures);
    }
    // NOTE: length is size in bytes
    public record Enclosure(String url, String type, long length) {}
}
