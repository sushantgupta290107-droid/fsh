package com.prizz.stream;

import com.prizz.model.Bid;
import com.prizz.model.Request;

import java.util.ArrayList;
import java.util.List;

// Syllabus: Lectures 43-46 - Lambda Expressions, Functional Interfaces, Method References, and Stream Pipelines
public class AnalyticsReportService {

    // Syllabus: Lectures 43-44 - Using custom Functional Interface with Lambda syntax
    public static List<Bid> filterBidsCustom(List<Bid> bids, BidFilter filter) {
        List<Bid> result = new ArrayList<>();
        for (Bid b : bids) {
            // Syllabus: Lectures 43-44 - Lambda invocation
            if (filter.accept(b)) {
                result.add(b);
            }
        }
        return result;
    }

    // Syllabus: Lectures 45-46 - Stream pipeline with filter(), sorted(), limit()
    public static void displayTopCheapestBids(List<Bid> bids, double maxBudget, int topN) {
        System.out.println("\n--- [Stream API] Top " + topN + " Lowest Bids Below Budget $" + maxBudget + " ---");
        // Syllabus: Lectures 45-46 - Stream pipeline: filter() -> sorted() -> limit()
        // Syllabus: Lectures 43-44 - Lambda syntax (b -> ...) and method reference (System.out::println)
        bids.stream()
                .filter(b -> b.getAmount() <= maxBudget)
                .sorted() // Natural ordering from Comparable<Bid>
                .limit(topN)
                .forEach(System.out::println); // Syllabus: Terminal operation to consume stream
    }

    // Syllabus: Lectures 45-46 - Stream pipeline with map(), distinct(), sorted()
    public static void displayUniqueCategories(List<Request> requests) {
        System.out.println("\n--- [Stream API] Unique Request Categories ---");
        // Syllabus: Lectures 45-46 - Stream pipeline: map() -> distinct() -> sorted()
        // Syllabus: Lectures 43-44 - Method reference (Request::getCategory)
        requests.stream()
                .map(Request::getCategory)
                .distinct()
                .sorted()
                .forEach(cat -> System.out.println(" • " + cat));
    }

    // Syllabus: Lectures 45-46 - Stream pipeline with sorted(), skip(), limit(), map()
    public static void displayRunnerUpBids(List<Bid> bids) {
        System.out.println("\n--- [Stream API] Runner-up Bids (Skipping Lowest Bid) ---");
        // Syllabus: Lectures 45-46 - Stream pipeline: sorted() -> skip(1) -> limit(3) -> map()
        bids.stream()
                .sorted()
                .skip(1) // Skips the 1st (cheapest) bid
                .limit(3)
                .map(b -> "Runner-Up Bid: $" + b.getAmount() + " by Provider [" + b.getProviderId() + "]")
                .forEach(System.out::println);
    }
}
