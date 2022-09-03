# Application Design

## Application Functions

1. Users with permissions - students must be able to edit their own projects, and only their projects
2. WYSIWYG content editing - project pages should be easily editable by anyone
3. Media storage - studentants can upload recorded audio and images for branding
4. RSS Feeds - Podcast RSS feeds are generated for recorded audio, so its easily accessible


## Technology Stack Overview & Reasoning

### Java

It's a well known, battle-tested language taught in introductory CS classes.

### Web Framework: Javalin

The web framework will only be used to serve a few templates, and handle forms.
The main alternative would be Spring Boot, which seems overly complex for this use case.

### Templating Engine: JTE

Honestly, its just what I found when looking for something that integrated with Javalin.
Project seems active though, and it can pre-compile templates which is great.

### Media Storage: S3-like Object Store

Any S3 compatible API should work, we can probably get away with using a very small part of the S3 API surface so switching between providers won't be difficult.
Instead of an object store media files could also be stored on disk.
This may be cheaper, but I worry that expanding storage over time would be difficult, especially after the original maintainers move on.
Just paying /GB/month for storage will be easier to maintain in the long run

### Database: SQLite

SQLite has several advantages over Postgres, MySQL and friends:
1. Easy to back up (its just a single file!)
2. Less resource intensive, it helps keep costs down
3. Simplicity: its easy to get up and running, and to maintain in the future.
4. faster queries, if we use sqlite we don't have to cache as much in the application or a separate store like Redis.

Of course it also has a many drawbacks, although they aren't particularly bad for our use-case.
1. Lack of features, we don't need any especially complex SQL features though so this should be fine
2. writes are serialized, we don't need to write to the database that often (just when users are created or content is edited/uploaded)
3. it's not a server, its more difficult (not impossible) to have multiple instances of the application talk to a single SQLite database. Since this application probably won't need to have load balanced over multiple instances I doubt this will be a problem.

### Content Editor: tinyMCE or CKEditor 5, or QuillJS

All of these seem like pretty good options, but I really need to prototype them to see which is best for this purpose.
