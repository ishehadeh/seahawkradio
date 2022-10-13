@import java.time.format.DateTimeFormatter
@import net.seahawkradio.cms.models.PodcastMetadata
@import net.seahawkradio.cms.models.PodcastEpisode
@param PodcastMetadata podcast
@param PodcastEpisode[] episodes

<?xml version="1.0" encoding="UTF-8"?>
<rss version="2.0" xmlns:itunes="http://www.itunes.com/dtds/podcast-1.0.dtd">
    <channel>
        <title>${podcast.title()}</title>
        <description>${podcast.description()}</description>
        <language>${podcast.language()}</language>
        <copyright>${podcast.copyright()}</copyright>
        <pubDate>${podcast.pubDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)}</pubDate>
        <author>${podcast.author()}</author>
        <itunes:explicit>${podcast.explicit() ? "yes" : "no"}</itunes:explicit>
        <itunes:owner>
            <itunes:email>${podcast.owner().email()}</itunes:email>
            <itunes:name>${podcast.owner().name()}</itunes:name>
        </itunes:owner>
        @for (var category : podcast.itunesCategories())
        <itunes:category text="${category}" />
        @endfor

        @for (var ep : episodes)
        <item>
            <guid>${ep.id().toString()}</guid>
            <title>${ep.title()}</title>
            <description>${ep.description()}</description>
            <pubDate>${ep.pubDate().format(DateTimeFormatter.RFC_1123_DATE_TIME)}</pubDate>
            @for (var enc : ep.enclosures())
            <enclosure url="${enc.url()}" type="${enc.type()}" length="${enc.length()}" />
            @endfor
            <itunes:duration>${ep.duration().getSeconds()}</itunes:duration>
            @if (ep.explicit() != podcast.explicit())
            <itunes:explicit>${ep.explicit() ? "yes" : "no"}</itunes:explicit>
            @endif
        </item>
        @endfor
    </channel>
</rss>
