package com.javaapp3;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JavaApp3 extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // تحميل الواجهة الرئيسية
FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
Parent root = loader.load();
        Scene scene = new Scene(root);
         stage.setTitle("Apartment Renting Management System"); 
        stage.setScene(scene);
        stage.show();

        // اختبار الاتصال هنا (اختياري، للتجربة)
        Connection conn = testConnection();
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ميثود اختبار الاتصال
    public static Connection testConnection() {
        String url = "jdbc:postgresql://localhost:5432/asmaDB"; // غيري حسب اسم قاعدة البيانات عندك
        String user = "asma";
        String password = "123456";

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ تم الاتصال بقاعدة البيانات!");
            return conn;
        } catch (SQLException e) {
            System.out.println("❌ فشل الاتصال!");
            e.printStackTrace();
            return null;
        }
    }
}
