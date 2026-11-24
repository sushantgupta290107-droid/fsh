package com.prizz.stream;

import com.prizz.model.Bid;

// Syllabus: Lectures 43-44 - Functional Interface for Lambda Expressions and Method References
@FunctionalInterface
public interface BidFilter {
    // Single abstract method
    boolean accept(Bid bid);
}
