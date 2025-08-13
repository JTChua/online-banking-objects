-- Users table: stores account details -- Main user table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL,
    pin TEXT NOT NULL,
    createdDate TEXT NOT NULL DEFAULT (datetime('now'))  --Auto-set on registration
);

-- Login history table
CREATE TABLE user_logins (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    last_login TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (user_id) REFERENCES users(id)
);