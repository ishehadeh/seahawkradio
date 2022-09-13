@import java.time.format.DateTimeFormatter
@import seahawkradio.cms.PodcastMetadata
@param PodcastMetadata podcast

<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
    <channel>
        <title>${podcast.title()}</title>
        <description>${podcast.description()}</description>
        <language>${podcast.language()}</language>
        <copyright>${podcast.copyright()}</copyright>
        <pubDate>${podcast.pubDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)}</pubDate>
        <author>${podcast.author()}</author>
        @if (podcast.explicit())
        <itunes:explicit />
        @endif
        @if (podcast.owner().isPresent())
        <itunes:owner>
            <itunes:email>${podcast.owner().get().email()}</itunes:email>
            <itunes:name>${podcast.owner().get().name()}</itunes:name>
        </itunes:owner>
        @endif
        @for (var category : podcast.itunesCategories())
        <itunes:category text="${category}" />
        @endfor
    </channel>
</rss>
