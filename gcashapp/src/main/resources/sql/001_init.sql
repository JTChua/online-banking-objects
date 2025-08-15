-- Simple initialization script for debugging
-- Users table: stores account details
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    number TEXT NOT NULL UNIQUE,
    pin TEXT NOT NULL,
    token TEXT,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now'))
);

-- Balance table: stores user balances  
CREATE TABLE IF NOT EXISTS Balance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_ID INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0.0,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (user_ID) REFERENCES users(id) ON DELETE CASCADE
);