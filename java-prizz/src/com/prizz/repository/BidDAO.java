package com.prizz.repository;

import com.prizz.model.Bid;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 37-42 - CRUD and ResultSet processing for Bids
public class BidDAO implements GenericDAO<Bid, String> {

    @Override
    public void create(Bid bid) throws SQLException {
        String sql = "INSERT INTO bids (id, request_id, provider_id, amount, estimated_hours, proposal_notes, timestamp) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Syllabus: Lectures 37-39 - PreparedStatement with parameter binding
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, bid.getId());
            pstmt.setString(2, bid.getRequestId());
            pstmt.setString(3, bid.getProviderId());
            pstmt.setDouble(4, bid.getAmount());
            pstmt.setInt(5, bid.getEstimatedHours());
            pstmt.setString(6, bid.getProposalNotes());
            pstmt.setString(7, bid.getTimestamp());

            // Syllabus: Lectures 37-39 - executeUpdate()
            pstmt.executeUpdate();
        }
    }

    @Override
    public Bid findById(String id) throws SQLException {
        String sql = "SELECT id, request_id, provider_id, amount, estimated_hours, proposal_notes, timestamp FROM bids WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                // Syllabus: Lectures 40-42 - Cursor iteration
                if (rs.next()) {
                    return mapRowToBid(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Bid> findAll() throws SQLException {
        List<Bid> list = new ArrayList<>();
        String sql = "SELECT id, request_id, provider_id, amount, estimated_hours, proposal_notes, timestamp FROM bids";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Syllabus: Lectures 40-42 - ResultSet cursor iteration
            while (rs.next()) {
                list.add(mapRowToBid(rs));
            }
        }
        return list;
    }

    public List<Bid> findByRequestId(String requestId) throws SQLException {
        List<Bid> list = new ArrayList<>();
        String sql = "SELECT id, request_id, provider_id, amount, estimated_hours, proposal_notes, timestamp FROM bids WHERE request_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, requestId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToBid(rs));
                }
            }
        }
        return list;
    }

    @Override
    public void update(Bid bid) throws SQLException {
        String sql = "UPDATE bids SET amount = ?, estimated_hours = ?, proposal_notes = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, bid.getAmount());
            pstmt.setInt(2, bid.getEstimatedHours());
            pstmt.setString(3, bid.getProposalNotes());
            pstmt.setString(4, bid.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM bids WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    // Syllabus: Lectures 40-42 - Typed getters and Java-object mapping
    private Bid mapRowToBid(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String requestId = rs.getString("request_id");
        String providerId = rs.getString("provider_id");
        double amount = rs.getDouble("amount");
        int hours = rs.getInt("estimated_hours");
        String notes = rs.getString("proposal_notes");
        String timestamp = rs.getString("timestamp");

        return new Bid(id, requestId, providerId, amount, hours, notes, timestamp);
    }
}
