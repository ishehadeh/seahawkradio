# Application Design

## Application Functions

1. Users with permissions - students must be able to edit their own projects, and only their projects
2. WYSIWYG content editing - project pages should be easily editable by anyone
3. Media storage - studentants can upload recorded audio and images for branding
4. RSS Feeds - Podcast RSS feeds are generated for recorded audio, so its easily accessible 


## Technology Stack Overview & Reasoning

### Web Framework: Javalin

The web framework will only be used to serve a few templates, and handle forms.
The main alternative would be Spring Boot, which seems overly complex for this use case.


### Content Editor: tinyMCE or CKEditor 5, or QuillJS

All of these seem like pretty good options, but I really need to prototype them to see which is best for this purpose.