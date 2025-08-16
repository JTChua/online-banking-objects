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

-- Balance table: stores user balance information
-- Note: This matches the existing schema from FreshDatabaseSetup.java
CREATE TABLE IF NOT EXISTS Balance (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_ID INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0.0,
    createdDate TEXT NOT NULL DEFAULT (datetime('now')),
    updatedDate TEXT NOT NULL DEFAULT (datetime('now')),
    
    -- Foreign key constraint to users table
    FOREIGN KEY (user_ID) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_number ON users(number);
CREATE INDEX IF NOT EXISTS idx_balance_user_id ON Balance(user_ID);

-- Insert dummy users (using INSERT OR IGNORE to avoid conflicts)
INSERT OR IGNORE INTO users (id, name, email, number, pin) VALUES
    (1, 'John Doe', 'john.doe@email.com', '09123456789', '1234'),
    (2, 'Jane Smith', 'jane.smith@email.com', '09987654321', '5678'),
    (3, 'Bob Johnson', 'bob.johnson@email.com', '09111222333', '9876'),
    (4, 'Alice Brown', 'alice.brown@email.com', '09444555666', '4321'),
    (5, 'Charlie Wilson', 'charlie.wilson@email.com', '09777888999', '1111'),
    (6, 'Diana Lee', 'diana.lee@email.com', '09222333444', '2222'),
    (7, 'Edward Davis', 'edward.davis@email.com', '09555666777', '3333'),
    (8, 'Fiona Martinez', 'fiona.martinez@email.com', '09888999000', '4444'),
    (9, 'George Taylor', 'george.taylor@email.com', '09333444555', '5555'),
    (10, 'Helen Clark', 'helen.clark@email.com', '09666777888', '6666');

-- Insert balance dummy data (using INSERT OR IGNORE to avoid conflicts)
-- Note: The DatabaseUtil.seedBalanceData() method will handle this automatically
-- but we include some manual entries here for immediate testing
INSERT OR IGNORE INTO Balance (user_ID, amount) VALUES
    (1, 15000.50),
    (2, 8750.25),
    (3, 25000.00),
    (4, 500.75),
    (5, 12345.60),
    (6, 0.00),
    (7, 99999.99),
    (8, 3250.40),
    (9, 7800.80),
    (10, 18500.30);