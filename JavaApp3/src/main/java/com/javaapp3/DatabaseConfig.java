// DatabaseConfig.java
package com.javaapp3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.Statement;
import java.sql.ResultSet;
import com.javaapp3.TenantsViewController.Tenant; // <-- استيراد الكلاس الداخلي

public class DatabaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/asmaDB";
    private static final String USER = "asma";
    private static final String PASSWORD = "123456";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL JDBC Driver not found.", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    

   



    
}
