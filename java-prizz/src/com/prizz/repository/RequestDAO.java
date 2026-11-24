package com.prizz.repository;

import com.prizz.model.Request;
import com.prizz.model.RequestStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 37-42 - Statement & PreparedStatement, CRUD, and ResultSet processing
public class RequestDAO implements GenericDAO<Request, String> {

    @Override
    public void create(Request req) throws SQLException {
        String sql = "INSERT INTO requests (id, customer_id, title, description, category, budget, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        // Syllabus: Lectures 37-39 - PreparedStatement with parameter binding
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, req.getId());
            pstmt.setString(2, req.getCustomerId());
            pstmt.setString(3, req.getTitle());
            pstmt.setString(4, req.getDescription());
            pstmt.setString(5, req.getCategory());
            pstmt.setDouble(6, req.getBudget());
            pstmt.setString(7, req.getStatus().name());
            pstmt.setString(8, req.getCreatedAt());

            // Syllabus: Lectures 37-39 - executeUpdate()
            pstmt.executeUpdate();
        }
    }

    @Override
    public Request findById(String id) throws SQLException {
        String sql = "SELECT id, customer_id, title, description, category, budget, status, created_at FROM requests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);

            // Syllabus: Lectures 37-39 - executeQuery()
            try (ResultSet rs = pstmt.executeQuery()) {
                // Syllabus: Lectures 40-42 - Cursor iteration
                if (rs.next()) {
                    return mapRowToRequest(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Request> findAll() throws SQLException {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT id, customer_id, title, description, category, budget, status, created_at FROM requests";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Syllabus: Lectures 40-42 - ResultSet cursor iteration
            while (rs.next()) {
                list.add(mapRowToRequest(rs));
            }
        }
        return list;
    }

    @Override
    public void update(Request req) throws SQLException {
        String sql = "UPDATE requests SET title = ?, description = ?, category = ?, budget = ?, status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, req.getTitle());
            pstmt.setString(2, req.getDescription());
            pstmt.setString(3, req.getCategory());
            pstmt.setDouble(4, req.getBudget());
            pstmt.setString(5, req.getStatus().name());
            pstmt.setString(6, req.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM requests WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    // Syllabus: Lectures 40-42 - Typed getters and Java-object mapping
    private Request mapRowToRequest(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String customerId = rs.getString("customer_id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        String category = rs.getString("category");
        double budget = rs.getDouble("budget");
        String statusStr = rs.getString("status");
        String createdAt = rs.getString("created_at");

        RequestStatus status = RequestStatus.OPEN;
        try {
            status = RequestStatus.valueOf(statusStr);
        } catch (Exception ignored) {}

        return new Request(id, customerId, title, description, category, budget, status, createdAt);
    }
}
