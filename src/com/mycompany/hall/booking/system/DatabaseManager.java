// DatabaseManager.java
// Updated to support fetching room names with bookings, deleting bookings,
// and fetching all bookings for reports.
package com.mycompany.hall.booking.system;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private static final String URL = "jdbc:mysql://localhost:3306/hallbooking";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    public static Connection getConnection() {
         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
             Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             System.out.println("Database connection successful.");
             return conn;
         } catch (ClassNotFoundException | SQLException e) {
             System.err.println("Database connection error: " + e.getMessage());
             return null;
         }
     }


    /*==============================================*
     * USER METHODS                  *
     *==============================================*/

    public static User validateLogin(String email, String password) {
        Connection conn = getConnection();
        if (conn == null) return null;
        User user = null;
        String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            }
        } catch (SQLException e) {
            System.err.println("Login error: " + e.getMessage());
        }
        return user;
    }

    public static boolean registerUser(User user) {
        Connection conn = getConnection();
        if (conn == null) return false;
        String sql = "INSERT INTO Users (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("User registration error: " + e.getMessage());
            return false;
        }
    }
    
    public static User getUserByEmail(String email) {
        // Check if a user already exists before registration.
        Connection conn = getConnection();
        if (conn == null) return null;
        User user = null;
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
            }
        } catch (SQLException e) {
            System.err.println("Get user by email error: " + e.getMessage());
        }
        return user;
    }


    /*==============================================*
     * ROOM METHODS                 *
     *==============================================*/
    
    public static ArrayList<Room> getAvailableRooms() {
        Connection conn = getConnection();
        ArrayList<Room> rooms = new ArrayList<>();
        if (conn == null) return rooms;
        String sql = "SELECT * FROM Rooms ORDER BY name ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rooms.add(new Room(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("capacity")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Get available rooms error: " + e.getMessage());
        }
        return rooms;
    }

    
    public static boolean addRoom(Room room) {
        String sql = "INSERT INTO rooms (name, capacity) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, room.getName());
            stmt.setInt(2, room.getCapacity());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean updateRoom(int roomId, String name, int capacity) {
        String sql = "UPDATE rooms SET name = ?, capacity = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setInt(2, capacity);
            stmt.setInt(3, roomId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roomId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /*==============================================*
     * BOOKING METHODS                *
     *==============================================*/

    public static boolean createBooking(Booking booking) {
        Connection conn = getConnection();
        if (conn == null) return false;
        String sql = "INSERT INTO Bookings (user_id, room_id, date, start_time, end_time, purpose, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, booking.getUserId());
            ps.setInt(2, booking.getRoomId());
            ps.setDate(3, booking.getDate());
            ps.setTime(4, booking.getStartTime());
            ps.setTime(5, booking.getEndTime());
            ps.setString(6, booking.getPurpose());
            ps.setString(7, booking.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Create booking error: " + e.getMessage());
            return false;
        }
    }

    /**
     * UPDATED to fetch the room name using a JOIN for a more user-friendly display.
     */
    public static ArrayList<Booking> getBookingsByUser(int userId) {
        Connection conn = getConnection();
        ArrayList<Booking> bookings = new ArrayList<>();
        if (conn == null) return bookings;
        
        String sql = "SELECT b.*, r.name as room_name FROM Bookings b " +
                     "JOIN Rooms r ON b.room_id = r.id " +
                     "WHERE b.user_id = ? ORDER BY b.date DESC";
                     
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setRoomId(rs.getInt("room_id"));
                booking.setDate(rs.getDate("date"));
                booking.setStartTime(rs.getTime("start_time"));
                booking.setEndTime(rs.getTime("end_time"));
                booking.setPurpose(rs.getString("purpose"));
                booking.setStatus(rs.getString("status"));
                booking.setRoomName(rs.getString("room_name")); // Set the fetched room name
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Get bookings by user error: " + e.getMessage());
        }
        return bookings;
    }

    public static ArrayList<Booking> getPendingBookings() {
        Connection conn = getConnection();
        ArrayList<Booking> bookings = new ArrayList<>();
        if (conn == null) return bookings;
        String sql = "SELECT * FROM Bookings WHERE status = 'pending' ORDER BY date ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setRoomId(rs.getInt("room_id"));
                booking.setDate(rs.getDate("date"));
                booking.setStartTime(rs.getTime("start_time"));
                booking.setEndTime(rs.getTime("end_time"));
                booking.setPurpose(rs.getString("purpose"));
                booking.setStatus(rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Get pending bookings error: " + e.getMessage());
        }
        return bookings;
    }

    /**
     * NEW METHOD: Gets all bookings, used for admin reporting.
     */
    public static ArrayList<Booking> getAllBookings() {
        Connection conn = getConnection();
        ArrayList<Booking> bookings = new ArrayList<>();
        if (conn == null) return bookings;
        String sql = "SELECT * FROM Bookings ORDER BY date DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setId(rs.getInt("id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setRoomId(rs.getInt("room_id"));
                booking.setDate(rs.getDate("date"));
                booking.setStartTime(rs.getTime("start_time"));
                booking.setEndTime(rs.getTime("end_time"));
                booking.setPurpose(rs.getString("purpose"));
                booking.setStatus(rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Get all bookings error: " + e.getMessage());
        }
        return bookings;
    }

    public static boolean updateBookingStatus(int bookingId, String status) {
        Connection conn = getConnection();
        if (conn == null) return false;
        String sql = "UPDATE Bookings SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update booking status error: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * NEW METHOD: Deletes a booking, used for the "Cancel Booking" feature.
     */
    public static boolean deleteBooking(int bookingId) {
        Connection conn = getConnection();
        if (conn == null) return false;
        String sql = "DELETE FROM Bookings WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete booking error: " + e.getMessage());
            return false;
        }
    }
}

