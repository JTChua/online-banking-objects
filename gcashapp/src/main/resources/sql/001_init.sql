-- Users table: stores account details
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    firstName TEXT NOT NULL,
    lastName TEXT NOT NULL,
    userName TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),  -- ISO-8601 format
    lastLogin TEXT
);

-- Balance table: stores account balance per user
CREATE TABLE IF NOT EXISTS balance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    amount REAL NOT NULL DEFAULT 0.0,
    user_id INTEGER NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Transactions table: stores transaction history
CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL, -- e.g. "deposit", "withdrawal"
    amount REAL NOT NULL,
    timestamp TEXT NOT NULL DEFAULT (datetime('now')), -- ISO-8601
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
