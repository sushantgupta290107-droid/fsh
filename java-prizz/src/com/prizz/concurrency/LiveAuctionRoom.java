package com.prizz.concurrency;

import com.prizz.model.Bid;

// Syllabus: Lectures 32-33 - Inter-thread communication: wait(), notify(), notifyAll(), guarded blocks & producer-consumer coordination
public class LiveAuctionRoom {
    private Bid latestBid;
    private boolean hasNewBid = false;
    private boolean auctionClosed = false;
    private final String requestId;

    public LiveAuctionRoom(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    // Syllabus: Lectures 32-33 - Producer: Provider submits a bid into the room
    // Syllabus: Lectures 32-33 - Synchronized method providing correct monitor usage
    public synchronized void submitBid(Bid bid) throws InterruptedException {
        // Syllabus: Lectures 32-33 - Guarded block: while-loop condition check before wait()
        while (hasNewBid && !auctionClosed) {
            wait();
        }

        if (auctionClosed) {
            System.out.println("[Room " + requestId + "] Cannot submit bid: Auction is closed.");
            return;
        }

        this.latestBid = bid;
        this.hasNewBid = true;
        System.out.println("[Room " + requestId + "] [PRODUCER] New Bid arrived from Provider: " + bid.getProviderId() + " -> $" + bid.getAmount());

        // Syllabus: Lectures 32-33 - notifyAll() wakes up waiting consumers
        notifyAll();
    }

    // Syllabus: Lectures 32-33 - Consumer: Customer/System consumes and evaluates the bid
    // Syllabus: Lectures 32-33 - Synchronized method providing correct monitor usage
    public synchronized Bid consumeBid() throws InterruptedException {
        // Syllabus: Lectures 32-33 - Guarded block: while-loop condition check
        while (!hasNewBid && !auctionClosed) {
            wait();
        }

        if (hasNewBid) {
            Bid bid = this.latestBid;
            this.hasNewBid = false;
            System.out.println("[Room " + requestId + "] [CONSUMER] Processed Bid: " + bid.getId() + " for $" + bid.getAmount());

            // Syllabus: Lectures 32-33 - notifyAll() signals producers that room is ready for next bid
            notifyAll();
            return bid;
        }

        return null;
    }

    // Syllabus: Lectures 32-33 - Closing the auction and releasing all waiting threads
    public synchronized void closeAuction() {
        this.auctionClosed = true;
        System.out.println("[Room " + requestId + "] Auction is officially CLOSED.");
        notifyAll();
    }

    public synchronized boolean isClosed() {
        return auctionClosed;
    }
}
