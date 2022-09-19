package seahawkradio.cms;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
// NOTE: see https://podcasters.apple.com/support/1691-apple-podcasts-categories for a list of
// itunes categories

public record PodcastMetadata(UUID id, String title, String link, String description,
        String copyright, ZonedDateTime pubDate, String language, boolean explicit,
        Set<String> itunesCategories, String author, Identity owner) {

}
