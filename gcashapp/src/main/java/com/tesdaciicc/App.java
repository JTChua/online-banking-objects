package com.tesdaciicc;

import com.tesdaciicc.data.util.DatabaseUtil;
import com.tesdaciicc.data.util.ConnectionFactory;
import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");

        // 1) Boot DB (create folder, run migrations)
        DatabaseUtil.initialize();

        // 2) Test a connection (optional)
        try (Connection c = ConnectionFactory.get()) {
            System.out.println("Connected to SQLite: " + c.getMetaData().getURL());
            System.out.println("Established Connection");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
