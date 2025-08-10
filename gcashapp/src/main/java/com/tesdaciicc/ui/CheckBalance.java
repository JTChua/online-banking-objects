package com.tesdaciicc.ui;

import com.tesdaciicc.service.CheckBalanceService;

//this should be inside of the homepage if successfully login

public class CheckBalance {

  private static final CheckBalanceService checkBalance = new CheckBalanceService();

  public static void getCheckBalance() {

    int testUserId = 1;
    double balance = checkBalance.checkBalance(testUserId);

    if (balance >= 0) {
      System.out.println("User " + testUserId + " balance: ₱" + balance);
    } else {
      System.out.println("Balance not found for User ID: " + testUserId);
    }

  }

}
