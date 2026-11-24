package com.prizz.concurrency;

import com.prizz.model.Bid;

// Syllabus: Lectures 32-33 - Producer-Consumer coordination: Producer Worker (Runnable)
public class ProviderBiddingWorker implements Runnable {
    private final LiveAuctionRoom room;
    private final String providerId;
    private final double bidAmount;
    private final int estimatedHours;
    private final String notes;

    public ProviderBiddingWorker(LiveAuctionRoom room, String providerId, double bidAmount, int estimatedHours, String notes) {
        this.room = room;
        this.providerId = providerId;
        this.bidAmount = bidAmount;
        this.estimatedHours = estimatedHours;
        this.notes = notes;
    }

    @Override
    public void run() {
        try {
            // Simulate research / thinking delay
            Thread.sleep((long) (Math.random() * 800 + 200));

            String bidId = "BID-" + System.currentTimeMillis() % 10000;
            String timestamp = String.valueOf(System.currentTimeMillis());
            Bid bid = new Bid(bidId, room.getRequestId(), providerId, bidAmount, estimatedHours, notes, timestamp);

            // Syllabus: Lectures 32-33 - Inter-thread communication call
            room.submitBid(bid);
        } catch (InterruptedException e) {
            System.err.println("[Worker] " + providerId + " was interrupted.");
            Thread.currentThread().interrupt();
        }
    }
}
