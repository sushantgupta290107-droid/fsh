package com.prizz.model;

// Syllabus: Core Java OOP - Concrete Subclass extending User
public class Provider extends User {
    private String skillCategory;
    private double rating;
    private int completedJobs;
    private double totalEarnings;

    public Provider(String id, String name, String email, String phone, String skillCategory, double rating, int completedJobs, double totalEarnings) {
        super(id, name, email, phone, "PROVIDER");
        this.skillCategory = skillCategory;
        this.rating = rating;
        this.completedJobs = completedJobs;
        this.totalEarnings = totalEarnings;
    }

    public String getSkillCategory() { return skillCategory; }
    public void setSkillCategory(String skillCategory) { this.skillCategory = skillCategory; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getCompletedJobs() { return completedJobs; }
    public void setCompletedJobs(int completedJobs) { this.completedJobs = completedJobs; }

    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }

    @Override
    public void displayProfile() {
        System.out.println("=== Service Provider Profile ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Category: " + skillCategory);
        System.out.println("Rating: " + rating + " / 5.0");
        System.out.println("Completed Jobs: " + completedJobs);
        System.out.println("Total Earnings: $" + totalEarnings);
    }
}
