// Room.java
// This class represents the Room data model, matching the 'Rooms' table in the database.
package com.mycompany.hall.booking.system;

public class Room {
    private int id;
    private String name;
    private int capacity;

    /**
     * Fully parameterized constructor.
     */
    public Room(int id, String name, int capacity) {
        this.id = id;
        this.name = name;
        this.capacity = capacity;
    }

    /**
     * Default constructor.
     */
    public Room() {
    }

    // Standard Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    // Standard Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;}
}