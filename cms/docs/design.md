# Application Design

## Application Functions

1. Users with permissions - students must be able to edit their own projects, and only their projects
2. WYSIWYG content editing - project pages should be easily editable by anyone
3. Media storage - studentants can upload recorded audio and images for branding
4. RSS Feeds - Podcast RSS feeds are generated for recorded audio, so its easily accessible 


## Technology Stack Overview & Reasoning

### Web Framework: Javalin

The web framework will only be used to serve a fairly small REST API for the frontend, along with a few RSS feeds.
The main alternative would be Spring Boot, which seems overly complex for this use case.