CREATE TABLE IF NOT EXISTS users (
    id TEXT PRIMARY KEY,
    username TEXT NOT NULL,
    email TEXT NOT NULL,
    email_normalized TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL
);
