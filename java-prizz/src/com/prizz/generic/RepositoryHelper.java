package com.prizz.generic;

import com.prizz.model.User;
import java.util.List;

// Syllabus: Lectures 47-48 - Generic method with bounded type and wildcard awareness
public class RepositoryHelper {

    // Syllabus: Lectures 47-48 - Generic method with bounded type parameter (<T extends Number>)
    public static <T extends Number> double calculatePercentage(T part, T total) {
        if (total.doubleValue() == 0.0) {
            return 0.0;
        }
        return (part.doubleValue() / total.doubleValue()) * 100.0;
    }

    // Syllabus: Lectures 47-48 - Upper-bounded wildcard (? extends User)
    public static void displayUsersSummary(List<? extends User> users) {
        System.out.println("--- User Directory Summary (" + users.size() + " accounts) ---");
        for (User u : users) {
            System.out.println(" -> " + u.toString());
        }
    }
}
