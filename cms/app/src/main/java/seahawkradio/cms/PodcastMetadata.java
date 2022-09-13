package seahawkradio.cms;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;

public record PodcastMetadata(UUID id, String title, String link, String description,
        String copyright, ZonedDateTime pubDate, String language, boolean explicit) {

}
