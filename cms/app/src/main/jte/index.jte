@import java.util.Optional
@import seahawkradio.cms.User
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
</head>

<body>
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
    </main>

    <footer></footer>
</body>

</html>
