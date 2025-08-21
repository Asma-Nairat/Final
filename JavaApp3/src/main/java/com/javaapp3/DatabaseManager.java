package com.javaapp3;

import com.javaapp3.TenantsViewController.Tenant;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.javaapp3.ApartmentsViewController.Apartment;

public class DatabaseManager {

    /**
     * دالة لجلب كل المستأجرين من قاعدة البيانات.
     * أعمدة الجدول: id, name, email, phone, occupation, address, status
     */
    public static ObservableList<Tenant> getAllTenants() {
        ObservableList<Tenant> tenants = FXCollections.observableArrayList();
        String sql = "SELECT id, name, email, phone, occupation, address, status FROM tenants";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tenant tenant = new Tenant(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("occupation"),
                        rs.getString("address"),
                        rs.getString("status")
                );
                tenants.add(tenant);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching tenants from DB: " + e.getMessage());
            e.printStackTrace();
        }
        return tenants;
    }

    /**
     * دالة لإضافة مستأجر جديد إلى قاعدة البيانات.
     */
    public static boolean addTenant(Tenant newTenant) {
        String sql = "INSERT INTO tenants (name, email, phone, occupation, address, status) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newTenant.getName());
            pstmt.setString(2, newTenant.getEmail());
            pstmt.setString(3, newTenant.getPhone());
            pstmt.setString(4, newTenant.getOccupation());
            pstmt.setString(5, newTenant.getAddress());
            pstmt.setString(6, newTenant.getStatus());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Database Error during tenant insertion: " + e.getMessage());
            return false;
        }
    }

    /**
     * دالة لحساب العدد الإجمالي للمالكين (owners).
     */
    public static int getOwnersCount() {
        String sql = "SELECT COUNT(*) FROM owners";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting owners count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * دالة لحساب العدد الإجمالي للمستأجرين (tenants).
     */
    public static int getTenantsCount() {
        String sql = "SELECT COUNT(*) FROM tenants";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting tenants count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * دالة لحساب العدد الإجمالي للشقق (apartments).
     */
    public static int getApartmentsCount() {
        String sql = "SELECT COUNT(*) FROM apartments";
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error getting apartments count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * دالة لجلب كل الشقق من قاعدة البيانات.
     * أعمدة الجدول: id, owner_id, name, type, town, location, description, image_path, status
     */
    public static ObservableList<Apartment> getAllApartments() {
        ObservableList<Apartment> apartments = FXCollections.observableArrayList();
        String sql = "SELECT id, owner_id, name, type, town, location, description, image_path, status FROM apartments";

        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // نستخدم البيانات الموجودة في الجدول ونكيفها مع كلاس Apartment
                Apartment apartment = new Apartment(
                        rs.getInt("id"),           // apartment_id
                        rs.getString("name"),      // address (استخدام name كعنوان)
                        rs.getString("town"),      // city  
                        0.0,                       // rent_price (افتراضي 0)
                        rs.getString("status") != null ? rs.getString("status") : "Available" // availability_status
                );
                apartments.add(apartment);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching apartments from DB: " + e.getMessage());
            e.printStackTrace();
        }
        return apartments;
    }

    /**
     * دالة لإضافة شقة جديدة إلى قاعدة البيانات.
     * نكيف المدخلات مع أعمدة الجدول الفعلية
     */
    public static boolean addApartment(String name, String type, String ownerName, String town, String location, String description, String imagePath) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // أولاً: البحث عن owner_id بناءً على اسم المالك
            int ownerId = -1;
            String ownerQuery = "SELECT id FROM owners WHERE name = ?";
            try (PreparedStatement ownerStmt = conn.prepareStatement(ownerQuery)) {
                ownerStmt.setString(1, ownerName);
                ResultSet rs = ownerStmt.executeQuery();
                if (rs.next()) {
                    ownerId = rs.getInt("id");
                } else {
                    System.err.println("Owner not found: " + ownerName);
                    return false;
                }
            }

            // ثانياً: إدراج الشقة
            String sql = "INSERT INTO apartments (owner_id, name, type, town, location, description, image_path, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Available')";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ownerId);
                pstmt.setString(2, name);
                pstmt.setString(3, type);
                pstmt.setString(4, town);
                pstmt.setString(5, location);
                pstmt.setString(6, description);
                pstmt.setString(7, imagePath);

                int affectedRows = pstmt.executeUpdate();
                return affectedRows > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error adding apartment to DB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}