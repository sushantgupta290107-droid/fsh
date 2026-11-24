package com.prizz.service;

import com.prizz.exception.AuctionClosedException;
import com.prizz.exception.InvalidBidException;
import com.prizz.exception.ResourceNotFoundException;
import com.prizz.model.Bid;
import com.prizz.model.Booking;
import com.prizz.model.BookingStatus;
import com.prizz.model.Request;
import com.prizz.model.RequestStatus;
import com.prizz.repository.BidDAO;
import com.prizz.repository.BookingDAO;
import com.prizz.repository.RequestDAO;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

// Syllabus: Core Java Collections Framework & Exception Handling coordinator
public class AuctionManager {
    private final RequestDAO requestDAO = new RequestDAO();
    private final BidDAO bidDAO = new BidDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    // Fast in-memory cache using HashMap
    private final Map<String, Request> requestCache = new HashMap<>();

    public void postNewRequest(Request request) throws SQLException {
        requestDAO.create(request);
        requestCache.put(request.getId(), request);
    }

    public List<Request> getActiveRequests() throws SQLException {
        List<Request> all = requestDAO.findAll();
        List<Request> active = new ArrayList<>();
        for (Request r : all) {
            if (r.getStatus() == RequestStatus.OPEN || r.getStatus() == RequestStatus.BIDDING_ACTIVE) {
                active.add(r);
            }
        }
        return active;
    }

    public void placeBid(Bid bid) throws PrizzServiceException, InvalidBidException, AuctionClosedException, ResourceNotFoundException {
        try {
            Request request = requestDAO.findById(bid.getRequestId());
            if (request == null) {
                // Syllabus: Core Java Custom Exception
                throw new ResourceNotFoundException("Service Request #" + bid.getRequestId() + " not found!");
            }

            if (request.getStatus() == RequestStatus.COMPLETED || request.getStatus() == RequestStatus.CANCELLED) {
                // Syllabus: Core Java Custom Exception
                throw new AuctionClosedException("Auction for request #" + request.getId() + " is already closed!");
            }

            if (bid.getAmount() <= 0) {
                // Syllabus: Core Java Custom Exception
                throw new InvalidBidException("Bid amount must be strictly greater than $0.00!");
            }

            if (bid.getAmount() > request.getBudget()) {
                // Syllabus: Core Java Custom Exception
                throw new InvalidBidException("Bid amount ($" + bid.getAmount() + ") cannot exceed maximum customer budget ($" + request.getBudget() + ")!");
            }

            // Persist bid via JDBC
            bidDAO.create(bid);

            // Update request status if not already active
            if (request.getStatus() == RequestStatus.OPEN) {
                request.setStatus(RequestStatus.BIDDING_ACTIVE);
                requestDAO.update(request);
            }

        } catch (SQLException e) {
            throw new PrizzServiceException("Database operation failed while placing bid: " + e.getMessage(), e);
        }
    }

    // Syllabus: Core Java Collections - Uses PriorityQueue to organize and return lowest bids
    public PriorityQueue<Bid> getRankedBidsForRequest(String requestId) throws SQLException {
        List<Bid> bids = bidDAO.findByRequestId(requestId);
        PriorityQueue<Bid> pq = new PriorityQueue<>(bids);
        return pq;
    }

    public Booking acceptWinningBid(String requestId, String winningBidId) throws Exception {
        Request request = requestDAO.findById(requestId);
        if (request == null) {
            throw new ResourceNotFoundException("Request not found: " + requestId);
        }

        Bid winningBid = bidDAO.findById(winningBidId);
        if (winningBid == null) {
            throw new ResourceNotFoundException("Bid not found: " + winningBidId);
        }

        // Close request
        request.setStatus(RequestStatus.ACCEPTED);
        requestDAO.update(request);

        // Create booking
        String bookingId = "BK-" + System.currentTimeMillis() % 10000;
        Booking booking = new Booking(
                bookingId,
                requestId,
                request.getCustomerId(),
                winningBid.getProviderId(),
                winningBid.getAmount(),
                BookingStatus.CONFIRMED,
                "2026-11-24"
        );

        bookingDAO.create(booking);
        return booking;
    }

    public static class PrizzServiceException extends Exception {
        public PrizzServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
