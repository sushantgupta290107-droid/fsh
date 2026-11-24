package com.prizz.concurrency;

// Syllabus: Lectures 32-33 - Timer thread controlling auction lifetime
public class AuctionTimerTask implements Runnable {
    private final LiveAuctionRoom room;
    private final int durationSeconds;

    public AuctionTimerTask(LiveAuctionRoom room, int durationSeconds) {
        this.room = room;
        this.durationSeconds = durationSeconds;
    }

    @Override
    public void run() {
        try {
            System.out.println("[Timer] Countdown started: " + durationSeconds + " seconds for Room " + room.getRequestId());
            for (int i = durationSeconds; i > 0; i--) {
                Thread.sleep(1000);
            }
            System.out.println("[Timer] Time expired! Closing auction room...");
            // Syllabus: Lectures 32-33 - Closes room and calls notifyAll()
            room.closeAuction();
        } catch (InterruptedException e) {
            System.out.println("[Timer] Timer was cancelled.");
            Thread.currentThread().interrupt();
        }
    }
}
