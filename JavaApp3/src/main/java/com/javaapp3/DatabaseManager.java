// ملف جديد: DatabaseManager.java
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
     * @return قائمة بالمستأجرين.
     */
    public static ObservableList<Tenant> getAllTenants() {
        ObservableList<Tenant> tenants = FXCollections.observableArrayList();
        String sql = "SELECT * FROM tenants";

        // لاحظي كيف نستخدم DatabaseConfig.getConnection() هنا
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
                        rs.getString("status") // نقرأ الحالة من قاعدة البيانات
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
     * @param newTenant الكائن الذي يحتوي على بيانات المستأجر الجديد.
     * @return true إذا نجحت العملية، وإلا false.
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
    
    // أضيفي هذه الدوال إلى كلاس DatabaseManager.java

/**
 * دالة لحساب العدد الإجمالي للمالكين (owners) في قاعدة البيانات. 
 * @return العدد الإجمالي للمالكين.
 */
public static int getOwnersCount() {
    String sql = "SELECT COUNT(*) FROM owners";
    try (Connection conn = DatabaseConfig.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) {
            return rs.getInt(1); // الحصول على القيمة من العمود الأول
        }
    } catch (SQLException e) {
        System.err.println("Error getting owners count: " + e.getMessage());
    }
    return 0; // إرجاع صفر في حال حدوث خطأ
}

/**
 * دالة لحساب العدد الإجمالي للمستأجرين (tenants).
 * @return العدد الإجمالي للمستأجرين.
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
 * @return العدد الإجمالي للشقق.
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
// أضيفي هاتين الدالتين إلى DatabaseManager.java

/**
 * دالة لجلب كل الشقق من قاعدة البيانات.
 * @return قائمة بالشقق.
 */
public static ObservableList<Apartment> getAllApartments() {
    ObservableList<Apartment> apartments = FXCollections.observableArrayList();
    String sql = "SELECT * FROM apartments";

    try (Connection conn = DatabaseConfig.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        while (rs.next()) {
            Apartment apartment = new Apartment(
                    rs.getInt("apartment_id"),
                    rs.getString("address"),
                    rs.getString("city"),
                    rs.getDouble("rent_price"),
                    rs.getString("availability_status")
            );
            apartments.add(apartment);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return apartments;
}

/**
 * دالة لإضافة شقة جديدة إلى قاعدة البيانات.
 * @param newApartment الكائن الذي يحتوي على بيانات الشقة الجديدة.
 * @return true إذا نجحت العملية.
 */
public static boolean addApartment(Apartment newApartment) {
    String sql = "INSERT INTO apartments (address, city, rent_price, availability_status, landlord_id) VALUES (?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConfig.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, newApartment.getAddress());
        pstmt.setString(2, newApartment.getCity());
        pstmt.setDouble(3, newApartment.getRentPrice());
        pstmt.setString(4, newApartment.getAvailabilityStatus());
        pstmt.setInt(5, 1); // ملاحظة: landlord_id حالياً ثابت (1)، يجب تعديله لاحقاً

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    // سنضيف دوال أخرى هنا لاحقاً (مثل getApartments, addPayment, etc.)
}