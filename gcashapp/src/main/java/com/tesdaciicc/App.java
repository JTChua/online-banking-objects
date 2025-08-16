package com.tesdaciicc;

import java.math.BigDecimal;

// import com.tesdaciicc.data.repository.UserAuthenticationDAO;
// import com.tesdaciicc.service.UserAuthenticationService;
// import com.tesdaciicc.model.UserAuthentication;

import com.tesdaciicc.model.Balance;

public class App {
    public static void main(String[] args) {

        Balance bal = new Balance(BigDecimal.valueOf(1000.00).setScale(2), 5);

        BigDecimal amt = bal.getAmount();
        System.out.println(amt.setScale(2));

    }
}