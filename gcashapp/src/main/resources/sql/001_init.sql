
-- For users table
CREATE TABLE IF NOT EXISTS users (
    userId INTEGER PRIMARY KEY AUTOINCREMENT,
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
    balanceId INTEGER PRIMARY KEY AUTOINCREMENT,
    userId INTEGER NOT NULL,
    balanceAmount REAL NOT NULL DEFAULT 0.00,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
    --accountNumber TEXT UNIQUE,
    FOREIGN KEY (userId) REFERENCES users(userId) ON DELETE CASCADE
);

-- For transactions table
CREATE TABLE IF NOT EXISTS transactions (
    transactionId INTEGER PRIMARY KEY AUTOINCREMENT,
    transactionAmount REAL NOT NULL CHECK (transactionAmount > 0.00),
    transactionName TEXT NOT NULL,
    userId INTEGER NOT NULL,
    transactionDate TEXT NOT NULL DEFAULT (datetime('now')),
    transferToAccountNo TEXT,
    transferFromAccountNo TEXT,
    accountNumber TEXT NOT NULL,
    FOREIGN KEY (userId) REFERENCES users(userId)
);