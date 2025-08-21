-- For users queries
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_number ON users(number);

-- For balance queries
--CREATE INDEX IF NOT EXISTS idx_balance_user_id ON balance(user_ID);
CREATE INDEX IF NOT EXISTS idx_balance_userId ON balance(userId);

-- For transactions history queries
CREATE INDEX IF NOT EXISTS idx_transactions_userId ON transactions(userId);
CREATE INDEX IF NOT EXISTS idx_transactions_accountNumber ON transactions(accountNumber);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transactionDate);