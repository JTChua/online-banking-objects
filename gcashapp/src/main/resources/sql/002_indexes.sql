CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_number ON users(number);
CREATE INDEX IF NOT EXISTS idx_balance_user_id ON balance(user_ID);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_transaction_userId ON transaction(userId);
CREATE INDEX IF NOT EXISTS idx_transaction_accountNumber ON transaction(accountNumber);
CREATE INDEX IF NOT EXISTS idx_transaction_date ON transaction(transactionDate);