package com.prizz.model;

// Syllabus: Core Java OOP - Concrete Subclass extending User
public class Admin extends User {
    private int clearanceLevel;

    public Admin(String id, String name, String email, String phone, int clearanceLevel) {
        super(id, name, email, phone, "ADMIN");
        this.clearanceLevel = clearanceLevel;
    }

    public int getClearanceLevel() { return clearanceLevel; }
    public void setClearanceLevel(int clearanceLevel) { this.clearanceLevel = clearanceLevel; }

    @Override
    public void displayProfile() {
        System.out.println("=== Administrator Profile ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Clearance Level: " + clearanceLevel);
    }
}
