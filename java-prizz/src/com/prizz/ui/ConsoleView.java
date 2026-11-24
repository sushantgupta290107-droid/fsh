package com.prizz.ui;

// Syllabus: Console Formatting Helper
public class ConsoleView {

    public static void printHeader(String title) {
        System.out.println("\n========================================================");
        System.out.println("  " + title.toUpperCase());
        System.out.println("========================================================");
    }

    public static void printComingSoon(String featureName) {
        System.out.println("\n[!] NOTICE: Feature '" + featureName + "' -> Coming soon");
    }

    public static void printMenuOption(int num, String label) {
        System.out.printf("  [%d] %s\n", num, label);
    }

    public static void printDivider() {
        System.out.println("--------------------------------------------------------");
    }
}
