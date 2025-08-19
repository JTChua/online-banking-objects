
-- For users table
CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT NOT NULL UNIQUE,
    number TEXT NOT NULL UNIQUE CHECK(length(number) = 11),
    pin TEXT NOT NULL CHECK(length(pin) BETWEEN 4 AND 6),
    token TEXT,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now'))
);

-- For balance table
CREATE TABLE IF NOT EXISTS balance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_ID INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0.0,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
    FOREIGN KEY (user_ID) REFERENCES users(id) ON DELETE CASCADE
);

-- For trasactions table
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