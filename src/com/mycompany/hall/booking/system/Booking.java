// Booking.java
package com.mycompany.hall.booking.system;

import java.sql.Date;
import java.sql.Time;

public class Booking {
    private int id;
    private int userId;
    private int roomId;
    private Date date;
    private Time startTime;
    private Time endTime;
    private String purpose;
    private String status;
    
    // This new field will hold the room's name, fetched via a DB join.
    private String roomName; 

    public Booking(int id, int userId, int roomId, Date date, Time startTime, Time endTime, String purpose, String status) {
        this.id = id;
        this.userId = userId;
        this.roomId = roomId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
        this.status = status;
    }

    public Booking() {
    }

    // Getters for all fields
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getRoomId() { return roomId; }
    public Date getDate() { return date; }
    public Time getStartTime() { return startTime; }
    public Time getEndTime() { return endTime; }
    public String getPurpose() { return purpose; }
    public String getStatus() { return status; }
    public String getRoomName() { return roomName; } // Getter for the new field

    // Setters for all fields
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
    public void setDate(Date date) { this.date = date; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }
    public void setPurpose(String purpose) { this.purpose = purpose; }
    public void setStatus(String status) { this.status = status; }
    public void setRoomName(String roomName) { this.roomName = roomName; } // Setter for the new field
}
