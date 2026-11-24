package com.prizz.repository;

import com.prizz.model.Booking;
import com.prizz.model.BookingStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 37-42 - CRUD and ResultSet processing for Bookings
public class BookingDAO implements GenericDAO<Booking, String> {

    @Override
    public void create(Booking booking) throws SQLException {
        String sql = "INSERT INTO bookings (id, request_id, customer_id, provider_id, agreed_amount, status, booked_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
        // Syllabus: Lectures 37-39 - PreparedStatement with parameter binding
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, booking.getId());
            pstmt.setString(2, booking.getRequestId());
            pstmt.setString(3, booking.getCustomerId());
            pstmt.setString(4, booking.getProviderId());
            pstmt.setDouble(5, booking.getAgreedAmount());
            pstmt.setString(6, booking.getStatus().name());
            pstmt.setString(7, booking.getBookedDate());

            pstmt.executeUpdate();
        }
    }

    @Override
    public Booking findById(String id) throws SQLException {
        String sql = "SELECT id, request_id, customer_id, provider_id, agreed_amount, status, booked_date FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToBooking(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Booking> findAll() throws SQLException {
        List<Booking> list = new ArrayList<>();
        String sql = "SELECT id, request_id, customer_id, provider_id, agreed_amount, status, booked_date FROM bookings";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // Syllabus: Lectures 40-42 - ResultSet cursor iteration
            while (rs.next()) {
                list.add(mapRowToBooking(rs));
            }
        }
        return list;
    }

    @Override
    public void update(Booking booking) throws SQLException {
        String sql = "UPDATE bookings SET status = ?, agreed_amount = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, booking.getStatus().name());
            pstmt.setDouble(2, booking.getAgreedAmount());
            pstmt.setString(3, booking.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM bookings WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        }
    }

    // Syllabus: Lectures 40-42 - Typed getters and Java-object mapping
    private Booking mapRowToBooking(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String requestId = rs.getString("request_id");
        String customerId = rs.getString("customer_id");
        String providerId = rs.getString("provider_id");
        double agreed = rs.getDouble("agreed_amount");
        String statusStr = rs.getString("status");
        String bookedDate = rs.getString("booked_date");

        BookingStatus status = BookingStatus.CONFIRMED;
        try {
            status = BookingStatus.valueOf(statusStr);
        } catch (Exception ignored) {}

        return new Booking(id, requestId, customerId, providerId, agreed, status, bookedDate);
    }
}
