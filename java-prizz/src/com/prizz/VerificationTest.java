package com.prizz;

import com.prizz.repository.DBConnection;
import com.prizz.ui.ConsoleView;
import com.prizz.ui.Main;

// Automated Verification Suite to test all syllabus features end-to-end
public class VerificationTest {
    public static void main(String[] args) {
        ConsoleView.printHeader("STARTING AUTOMATED SYLLABUS VERIFICATION SUITE");

        try {
            // Syllabus: Lectures 34-36 - Initialize SQLite database tables via JDBC
            DBConnection.initializeDatabase();

            // 1. Test JDBC Initialization & CRUD
            System.out.println("\n>>> [1/5] Testing JDBC & Database Programming (Lectures 34-42)...");
            Main.runJdbcDemo();

            // 2. Test Multithreading & Producer-Consumer
            System.out.println("\n>>> [2/5] Testing Concurrency, wait() & notify() (Lectures 32-33)...");
            Main.runConcurrencyDemo();

            // 3. Test Stream API & Lambdas
            System.out.println("\n>>> [3/5] Testing Stream API & Lambdas (Lectures 43-46)...");
            Main.runStreamApiDemo();

            // 4. Test Generics & Wildcards
            System.out.println("\n>>> [4/5] Testing Generics & Type Safety (Lectures 47-48)...");
            Main.runGenericsDemo();

            // 5. Test File I/O, CSV & StreamTokenizer
            System.out.println("\n>>> [5/5] Testing File I/O & CSV Collections (Lectures 49-54)...");
            Main.runFileIoDemo();

            // 6. Test Out-of-syllabus Coming Soon Placeholders
            System.out.println("\n>>> [6/6] Testing Out-of-Syllabus Placeholders...");
            ConsoleView.printComingSoon("Payment Gateway (Stripe)");
            ConsoleView.printComingSoon("WebSocket Realtime Sync");
            ConsoleView.printComingSoon("AI Provider Matcher");

            ConsoleView.printHeader("ALL SYLLABUS TESTS COMPLETED SUCCESSFULLY WITH 0 ERRORS");
        } catch (Throwable t) {
            System.err.println("[TEST FAILURE] Exception occurred during verification:");
            t.printStackTrace();
            System.exit(1);
        }
    }
}
