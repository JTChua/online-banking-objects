CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_number ON users(number);
CREATE INDEX IF NOT EXISTS idx_balance_user_id ON balance(user_ID);