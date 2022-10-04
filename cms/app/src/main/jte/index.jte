@import java.util.Optional
@import seahawkradio.cms.User
@import seahawkradio.cms.Language
@import seahawkradio.cms.ApplePodcastCategory

@param Optional<User> user

<!DOCTYPE html>
<html lang="en" dir="ltr" itemscope itemtype="https://schema.org/WebPage" prefix="og:http://ogp.me/ns#">

<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />

    <title>Seahawk Radio</title>

    <!-- TODO: favicon -->
    <!-- TODO: site metadatadata -->
    <!-- TODO: social media metadatadata -->

    <link rel="preload" href="/main.css" as="style" />
    <link rel="stylesheet" href="/main.css" />

    <script src="/editor.js"></script>
</head>

<body onload="Editor.makePodcastEditor(document.getElementById('editor'))">
    <header>
        <h1>Seahawk Radio</h1>
        <nav>
            <a href="/" rel="noopener noreferrer">Home</a>
        </nav>
        <time></time>
    </header>

    <main>
        @if (user.isEmpty())
        <h2>Login</h2>
        <form method="post" action="/login">
            <input type="text" name="username" placeholder="username" />
            <input type="text" name="password" placeholder="password" />
            <input type="submit" value="submit" />
        </form>
        @else
        <h2>Hello, ${user.get().username()}</h2>
        @endif

        <h2>Upload Image</h2>
        <form method="post" action="/upload-image" enctype="multipart/form-data">
            <input type="file" name="image" multiple />
            <input type="submit" value="submit" />
        </form>

        <h2>Upload Audio</h2>
        <form method="post" action="/upload-audio" enctype="multipart/form-data">
            <input type="file" name="audio" multiple />
            <input type="submit" value="submit" />
        </form>

        <div>
            <h2>Podcast</h2>
            <form action="/podcast" method="post">
                <fieldset>
                    <label for="podcast-title">Title</label>
                    <input type="text" id="podcast-title" name="title"/>

                    <label for="podcast-copyright">Copyright</label>
                    <input type="text" id="podcast-copyright" name="copyright"/>

                    <label for="podcast-language">Language</label>
                    <select id="podcast-language" name="language">
                        @for(var lang : Language.ALL)
                            @if (lang.alpha2().isPresent())
                                <option value="${lang.alpha2().get()}">${lang.name()}</option>
                            @endif
                        @endfor
                    </select>

                    <fieldset class="category-selector">
                        <legend>Category</legend>
                        @for(var category : ApplePodcastCategory.ALL)
                            !{var categoryId = "category-" + category.name().replaceAll("[\\W]+", "");}

                            <details>
                                <summary>
                                    <input type="checkbox" name="categories[]" id="${categoryId}" value="${category.name()}" />
                                    <label for="category-${category.name()}">${category.name()}</label>
                                </summary>
                                <ul>
                                    @for(var subcategory : category.subcategories())
                                        !{var subcategoryId = categoryId + "-" + subcategory.replaceAll("[\\W]+", "");}
                                        <li>
                                            <input type="checkbox" name="categories[]" id="${subcategoryId}" value="${category.name()}::${subcategory}" />
                                            <label for="${subcategoryId}">${subcategory}</label>
                                        </li>
                                    @endfor
                                </ul>
                            </details>
                        @endfor
                    </fieldset>
                    <label for="podcast-explicit">Explicit</label>
                    <input type="checkbox" id="podcast-explicit" name="explicit" />

                    <textarea name="description" id="editor">
                        Podcast description...
                    </textarea>

                    <input type="submit" value="Create" />
                </fieldset>
            </form>
            <div id="editor"></div>
        </div>
    </main>

    <footer></footer>
</body>

</html>
