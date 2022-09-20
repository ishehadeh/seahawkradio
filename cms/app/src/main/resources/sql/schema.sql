CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    email_normalized TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,
    created TEXT NOT NULL,
    updated TEXT NOT NULL,
    deleted TEXT
);

CREATE TABLE IF NOT EXISTS sessions (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    created TEXT NOT NULL,
    expires TEXT NOT NULL,

  FOREIGN KEY(user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS media (
    id TEXT PRIMARY KEY,
    filename TEXT NOT NULL,
    content_type TEXT NOT NULL,
    created TEXT NOT NULL,
    deleted TEXT
);

CREATE TABLE IF NOT EXISTS category (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
);

CREATE TABLE IF NOT EXISTS podcast (
    id TEXT PRIMARY KEY,
    title TEXT NOT NULL,
    link TEXT NOT NULL,
    description TEXT NOT NULL,
    copyright TEXT NOT NULL,
    pub_date TEXT NOT NULL,
    language TEXT NOT NULL,
    explicit INTEGER NOT NULL,
    author TEXT NOT NULL,
    owner_name TEXT,
    owner_email TEXT,
    created TEXT NOT NULL,
    updated TEXT NOT NULL,
    deleted TEXT
);

CREATE TABLE IF NOT EXISTS podcast_category (
    podcast_id TEXT NOT NULL,
    category_id INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS podcast_editors (
    podcast_id TEXT NOT NULL,
    user_id TEXT NOT NULL
)
