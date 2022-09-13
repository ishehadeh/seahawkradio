package seahawkradio.cms;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.UUID;

public record PodcastEpisode(UUID id, String title, String description, Duration duration,
        Enclosure enclosure, boolean explicit, ZonedDateTime pubDate) {
    // NOTE: length is size in bytes
    public record Enclosure(String url, String type, long length) {

    }
}
