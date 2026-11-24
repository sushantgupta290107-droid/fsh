package com.prizz.model;

// Syllabus: Core Java OOP - Concrete Subclass extending User
public class Customer extends User {
    private String address;
    private String preferredCategory;

    public Customer(String id, String name, String email, String phone, String address, String preferredCategory) {
        super(id, name, email, phone, "CUSTOMER");
        this.address = address;
        this.preferredCategory = preferredCategory;
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPreferredCategory() { return preferredCategory; }
    public void setPreferredCategory(String preferredCategory) { this.preferredCategory = preferredCategory; }

    @Override
    public void displayProfile() {
        System.out.println("=== Customer Profile ===");
        System.out.println("ID: " + getId());
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Address: " + address);
        System.out.println("Preferred Category: " + preferredCategory);
    }
}
