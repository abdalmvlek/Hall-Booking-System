// UserDashboard.java
// Updated to show more booking details and allow users to cancel their bookings.
package com.mycompany.hall.booking.system.ui;

import com.mycompany.hall.booking.system.Booking;
import com.mycompany.hall.booking.system.DatabaseManager;
import com.mycompany.hall.booking.system.Room;
import com.mycompany.hall.booking.system.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserDashboard extends JFrame {

    private final User currentUser;
    private JTable roomsTable;
    private JTable bookingsTable;
    private volatile boolean isRunning = true; // Flag to control the auto-refresh thread

    public UserDashboard(User user) {
        this.currentUser = user;
        
        setTitle("لوحة تحكم المستخدم - مرحباً, " + currentUser.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);
        
        // Add a listener to stop the background thread when the window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isRunning = false; // Signal the refresh thread to terminate
            }
        });


        // Main Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("القاعات المتاحة", createRoomsPanel());
        tabbedPane.addTab("حجوزاتي", createMyBookingsPanel());

        add(tabbedPane);
        
        // Load initial data into tables
        refreshRoomsTable();
        refreshBookingsTable();
        
        // Start the background thread for auto-refreshing
        startAutoRefresh();
    }
    
    /**
    * This method starts a background thread that refreshes the list
    * of pending bookings without freezing the user interface.
    */
    private void startAutoRefresh() {
        Thread refreshThread = new Thread(() -> {
            while (isRunning) {
                try {
                    Thread.sleep(30000); // Wait for 30 seconds
                    
                    // GUI updates must be done on the Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Auto-refreshing pending bookings...");
                        refreshBookingsTable();
                    });
                } catch (InterruptedException e) {
                    System.out.println("Auto-refresh thread was interrupted.");
                }
            }
            System.out.println("Auto-refresh thread has stopped.");
        });
        refreshThread.setDaemon(true); // Allows the app to close even if this thread is running
        refreshThread.start();
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Table setup for available rooms
        String[] columnNames = {"ID", "اسم القاعة", "السعة"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        roomsTable = new JTable(model);
        roomsTable.setFillsViewportHeight(true);
        roomsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomsTable.setRowHeight(25);
        
        panel.add(new JScrollPane(roomsTable), BorderLayout.CENTER);

        // Button to book a room
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("حجز القاعة المحددة");
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bookButton.addActionListener(e -> bookRoom());
        buttonPanel.add(bookButton);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createMyBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // column names for the user's bookings table
        String[] columnNames = {"ID الحجز", "اسم القاعة", "الغرض من الحجز", "التاريخ", "الحالة"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = new JTable(model);
        bookingsTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingsTable.setRowHeight(25);
        panel.add(new JScrollPane(bookingsTable), BorderLayout.CENTER);
        
        // NEW: Add a button to cancel a booking
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("إلغاء الحجز المحدد");
        cancelButton.setBackground(new Color(220, 53, 69)); // Red color for emphasis
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        cancelButton.addActionListener(e -> cancelBooking());
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * METHOD to handle the logic for canceling a booking.
     */
    private void cancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "الرجاء اختيار حجز لإلغائه.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String status = (String) bookingsTable.getValueAt(selectedRow, 4); // Status is at the 5th column (index 4)
        if ("pending".equalsIgnoreCase(status)) {
             JOptionPane.showMessageDialog(this, "لا يمكن إلغاء إلا الحجوزات التي ما زالت 'قيد الانتظار'.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) bookingsTable.getValueAt(selectedRow, 0); // Booking ID is at the 1st column (index 0)
        int choice = JOptionPane.showConfirmDialog(this, "هل أنت متأكد أنك تريد إلغاء هذا الحجز؟", "تأكيد الإلغاء", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteBooking(bookingId)) {
                JOptionPane.showMessageDialog(this, "تم إلغاء الحجز بنجاح.", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                refreshBookingsTable(); // Refresh the table to show the change
            } else {
                JOptionPane.showMessageDialog(this, "فشل إلغاء الحجز.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void bookRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "الرجاء اختيار قاعة للحجز.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int roomId = (int) roomsTable.getValueAt(selectedRow, 0);

        // Create a custom dialog for booking details
        JTextField purposeField = new JTextField();
        JTextField dateField = new JTextField("YYYY-MM-DD");
        JTextField startTimeField = new JTextField("HH:MM:SS");
        JTextField endTimeField = new JTextField("HH:MM:SS");
        
        final JComponent[] inputs = new JComponent[] {
                new JLabel("الغرض من الحجز"),
                purposeField,
                new JLabel("التاريخ (YYYY-MM-DD)"),
                dateField,
                new JLabel("وقت البدء (HH:MM:SS)"),
                startTimeField,
                new JLabel("وقت الانتهاء (HH:MM:SS)"),
                endTimeField
        };
        
        int result = JOptionPane.showConfirmDialog(this, inputs, "إدخال تفاصيل الحجز", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Validate that the date is not in the past
                Date bookingDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText());
                Date today = new Date();
                Date todayWithoutTime = new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd").format(today));
                
                if (bookingDate.before(todayWithoutTime)) {
                    JOptionPane.showMessageDialog(this, "لا يمكن الحجز في تاريخ قد مضى.", "خطأ في التاريخ", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create and save the new booking
                Booking newBooking = new Booking();
                newBooking.setUserId(currentUser.getId());
                newBooking.setRoomId(roomId);
                newBooking.setPurpose(purposeField.getText());
                newBooking.setDate(java.sql.Date.valueOf(dateField.getText()));
                newBooking.setStartTime(java.sql.Time.valueOf(startTimeField.getText()));
                newBooking.setEndTime(java.sql.Time.valueOf(endTimeField.getText()));
                newBooking.setStatus("pending"); // All new bookings are pending approval

                if (DatabaseManager.createBooking(newBooking)) {
                    JOptionPane.showMessageDialog(this, "تم إرسال طلب الحجز بنجاح وهو الآن قيد الانتظار للموافقة.", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                    refreshBookingsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "فشل إرسال طلب الحجز.", "خطأ", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException | ParseException ex) {
                JOptionPane.showMessageDialog(this, "صيغة التاريخ أو الوقت غير صالحة. الرجاء استخدام YYYY-MM-DD و HH:MM:SS.", "خطأ في الصيغة", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshRoomsTable() {
        DefaultTableModel model = (DefaultTableModel) roomsTable.getModel();
        model.setRowCount(0);
        ArrayList<Room> rooms = DatabaseManager.getAvailableRooms();
        for (Room room : rooms) {
            model.addRow(new Object[]{room.getId(), room.getName(), room.getCapacity()});
        }
    }

    /**
     * UPDATED to populate the table with the new, more descriptive data.
     */
    private void refreshBookingsTable() {
        DefaultTableModel model = (DefaultTableModel) bookingsTable.getModel();
        model.setRowCount(0);
        ArrayList<Booking> bookings = DatabaseManager.getBookingsByUser(currentUser.getId());
        for (Booking booking : bookings) {
            model.addRow(new Object[]{
                booking.getId(), 
                booking.getRoomName(),  // Show room name
                booking.getPurpose(),   // Show booking purpose
                booking.getDate(), 
                booking.getStatus()
            });
        }
    }
}

