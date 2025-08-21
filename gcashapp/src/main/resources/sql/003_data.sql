
-- Insert dummy users
INSERT OR IGNORE INTO users (userId, name, email, number, pin) VALUES
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

-- Insert balance dummy data
INSERT OR IGNORE INTO balance (userId, balanceAmount) VALUES
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

    INSERT INTO transactions (transactionAmount, transactionName, userId, transferToAccountNo, transferFromAccountNo, accountNumber)
VALUES 
    (100.00, 'John Doe', 1, '1001', 'CASH_IN_SOURCE', '1001'),
    (1000.00, 'Jane Smith', 1, '1001', 'CASH_IN_SOURCE', '1001'),
    (300.00, 'Bob Johnson', 2, '1002', 'CASH_IN_SOURCE', '1002'),
    (3500.00, 'Alice Brown', 2, '1002', 'CASH_IN_SOURCE', '1002');