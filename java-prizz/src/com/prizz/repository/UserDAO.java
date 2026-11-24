package com.prizz.repository;

import com.prizz.model.Admin;
import com.prizz.model.Customer;
import com.prizz.model.Provider;
import com.prizz.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 37-42 - PreparedStatement, parameter binding, CRUD, ResultSet typed getters and Java-object mapping
public class UserDAO implements GenericDAO<User, String> {

    @Override
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO users (id, name, email, phone, role, extra1, extra2) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Syllabus: Lectures 37-39 - PreparedStatement, parameter binding and try-with-resources
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getRole());

            if (user instanceof Customer) {
                Customer c = (Customer) user;
                pstmt.setString(6, c.getAddress());
                pstmt.setString(7, c.getPreferredCategory());
            } else if (user instanceof Provider) {
                Provider p = (Provider) user;
                pstmt.setString(6, p.getSkillCategory());
                pstmt.setString(7, String.valueOf(p.getRating()));
            } else if (user instanceof Admin) {
                Admin a = (Admin) user;
                pstmt.setString(6, String.valueOf(a.getClearanceLevel()));
                pstmt.setString(7, "");
            } else {
                pstmt.setString(6, "");
                pstmt.setString(7, "");
            }

            // Syllabus: Lectures 37-39 - executeUpdate() for insert
            pstmt.executeUpdate();
        }
    }

    @Override
    public User findById(String id) throws SQLException {
        String sql = "SELECT id, name, email, phone, role, extra1, extra2 FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Syllabus: Lectures 37-39 - parameter binding
            pstmt.setString(1, id);

            // Syllabus: Lectures 37-39 - executeQuery()
            try (ResultSet rs = pstmt.executeQuery()) {
                // Syllabus: Lectures 40-42 - cursor iteration and typed getters
                if (rs.next()) {
                    return mapRowToUser(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, phone, role, extra1, extra2 FROM users";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Syllabus: Lectures 40-42 - ResultSet cursor iteration
            while (rs.next()) {
                list.add(mapRowToUser(rs));
            }
        }
        return list;
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getId());
            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    // Syllabus: Lectures 40-42 - Typed getters and Java-object mapping
    private User mapRowToUser(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String name = rs.getString("name");
        String email = rs.getString("email");
        String phone = rs.getString("phone");
        String role = rs.getString("role");
        String extra1 = rs.getString("extra1");
        String extra2 = rs.getString("extra2");

        if ("CUSTOMER".equalsIgnoreCase(role)) {
            return new Customer(id, name, email, phone, extra1, extra2);
        } else if ("PROVIDER".equalsIgnoreCase(role)) {
            double rating = 5.0;
            try {
                if (extra2 != null && !extra2.isEmpty()) {
                    rating = Double.parseDouble(extra2);
                }
            } catch (NumberFormatException ignored) {}
            return new Provider(id, name, email, phone, extra1, rating, 0, 0.0);
        } else {
            int level = 1;
            try {
                if (extra1 != null && !extra1.isEmpty()) {
                    level = Integer.parseInt(extra1);
                }
            } catch (NumberFormatException ignored) {}
            return new Admin(id, name, email, phone, level);
        }
    }
}
