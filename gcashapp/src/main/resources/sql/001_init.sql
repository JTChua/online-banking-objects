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

CREATE TABLE IF NOT EXISTS balance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_ID INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0.0,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (user_ID) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS transaction (
    transactionId INTEGER PRIMARY KEY AUTOINCREMENT,
    transactionAmount REAL NOT NULL CHECK (transactionAmount > 0),
    name TEXT NOT NULL,
    userId INTEGER NOT NULL,
    transactionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transferToAccountNo TEXT,
    transferFromAccountNo TEXT,
    accountNumber TEXT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(id)
);