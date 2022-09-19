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
