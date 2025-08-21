package com.tesdaciicc;

import com.tesdaciicc.service.CashInService;
import com.tesdaciicc.model.CashIn;
import com.tesdaciicc.model.Balance;
import com.tesdaciicc.model.UserAuthentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class CashInTest {

    public static void main(String[] args) {
        System.out.println("=== CashInService Test Runner ===");

        CashInService cashInService = new CashInService();

        String testAccount = "09123456789";   // Change if your DB uses different account no.
        String userName = "John Doe";

        // 1. Validate account
        System.out.println("\n[1] Validating account...");
        boolean valid = cashInService.validateAccount(testAccount);
        System.out.println("Account valid? " + valid);
        if (!valid) {
            System.out.println("❌ Test account not found. Please insert account in DB before running.");
            return;
        }

        // 2. Show current balance
        Optional<Balance> balanceOpt = cashInService.getCurrentBalance(testAccount);
        balanceOpt.ifPresent(balance ->
                System.out.println("Current Balance: " + balance.getAmount()));

        // 3. Perform cash-in
        System.out.println("\n[2] Performing cash-in of 500.00...");
        boolean success = cashInService.processCashIn(testAccount, BigDecimal.valueOf(500), userName);
        System.out.println("Cash-in success? " + success);

        // 4. Check updated balance
        balanceOpt = cashInService.getCurrentBalance(testAccount);
        balanceOpt.ifPresent(balance ->
                System.out.println("Updated Balance: " + balance.getAmount()));

        // 5. Show transaction history
        System.out.println("\n[3] Transaction history for account: " + testAccount);
        Optional<UserAuthentication> userOpt = cashInService.getUserByAccountNumber(testAccount);
        if (userOpt.isPresent()) {
            List<CashIn> transactions = cashInService.getTransactionHistory(userOpt.get().getId());
            for (CashIn tx : transactions) {
                System.out.println(tx);
            }
        }

        // 6. Show total cash-in
        System.out.println("\n[4] Total Cash-In for account: " + testAccount);
        if (userOpt.isPresent()) {
            BigDecimal total = cashInService.getTotalCashIn(userOpt.get().getId());
            System.out.println("Total Cash-In Amount: " + total);
        }

        System.out.println("\n=== Test Completed ===");
    }

}
