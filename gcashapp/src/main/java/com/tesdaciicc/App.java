package com.tesdaciicc;

import com.tesdaciicc.data.util.Config;

import java.io.InputStream;
// import java.math.BigDecimal;


import com.tesdaciicc.data.util.DatabaseUtil;

// import com.tesdaciicc.data.repository.UserAuthenticationDAO;
// import com.tesdaciicc.service.UserAuthenticationService;
// import com.tesdaciicc.model.UserAuthentication;

// import com.tesdaciicc.model.Balance;

public class App {
    public static void main(String[] args) {
        
        try {

            System.out.println("Resource exists: " + 
            (DatabaseUtil.class.getResourceAsStream("/sql/001_init.sql") != null));

            System.out.println("Testing resource loading...");
            InputStream testStream = DatabaseUtil.class.getResourceAsStream(Config.INIT_SQL_FILE);
            if (testStream == null) {
                System.err.println("ERROR: Failed to load " + Config.INIT_SQL_FILE);
                System.err.println("Tried to load from: " + 
                    DatabaseUtil.class.getResource(Config.INIT_SQL_FILE));
            } else {
                System.out.println("Successfully loaded SQL file");
                testStream.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}