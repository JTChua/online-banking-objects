package com.tesdaciicc;

// import com.tesdaciicc.ui.AccountSecurity;
// import com.tesdaciicc.ui.Login;

// import java.time.LocalDateTime;

import com.tesdaciicc.model.UserAuthentication;
// import com.tesdaciicc.service.UserAuthenticationService;
// import com.tesdaciicc.ui.Registration;

// import com.tesdaciicc.ui.CheckBalance;
// import com.tesdaciicc.service.CheckBalanceService;
// import com.tesdaciicc.data.repository.UserAuthenticationDAO;

// import java.util.Scanner;
// import java.time.LocalDateTime;

// import com.tesdaciicc.data.util.DatabaseUtil;

public class App {

    public static void main(String[] args) {

        UserAuthentication user = new UserAuthentication("Linda A. Delapena", "LindaADelapena@armyspy.com",
                "09164784125", "1234");

        System.out.println(user.toString());

    }
}
