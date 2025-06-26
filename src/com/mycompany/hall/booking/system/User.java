// User.java
// This class represents the User data model, matching the 'Users' table in the database.
package com.mycompany.hall.booking.system;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role; // Can be "user" or "admin"

    /**
     * Fully parameterized constructor.
     */
    public User(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Default constructor.
     */
    public User() {
    }

    // Standard Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Standard Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
