package com.mycompany.hall.booking.system.ui;

import com.mycompany.hall.booking.system.Booking;
import com.mycompany.hall.booking.system.DatabaseManager;
import com.mycompany.hall.booking.system.Room;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {

    private JTable roomsTable;
    private JTable pendingBookingsTable;
    private JTable allBookingsTable;
    private volatile boolean isRunning = true; // Flag to control the auto-refresh thread

    public AdminDashboard() {
        setTitle("لوحة تحكم المسؤول - إدارة النظام");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        // listener to stop the background thread when the window is closed
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                isRunning = false; // Signal the refresh thread to terminate
            }
        });

        // Main Tabbed Pane for organizing admin tasks
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        tabbedPane.addTab("إدارة القاعات", createManageRoomsPanel());
        tabbedPane.addTab("مراجعة الطلبات المعلقة", createReviewBookingsPanel());
        tabbedPane.addTab("عرض كل الحجوزات", createAllBookingsPanel());

        add(tabbedPane);

        // Load initial data for all tables
        refreshAdminRoomsTable();
        refreshPendingBookingsTable();
        refreshAllBookingsTable();
        
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
                        refreshPendingBookingsTable();
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

    private JPanel createManageRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] columnNames = {"ID", "اسم القاعة", "السعة"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomsTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(roomsTable);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("إضافة قاعة");
        JButton editButton = new JButton("تعديل القاعة");
        JButton deleteButton = new JButton("حذف القاعة");

        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editRoom());
        deleteButton.addActionListener(e -> deleteRoom());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }


    
    

    private JPanel createReviewBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"ID الحجز", "ID المستخدم", "ID القاعة", "التاريخ", "الغرض", "الحالة"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
             public boolean isCellEditable(int row, int column) { return false; }
        };
        pendingBookingsTable = new JTable(model);
        panel.add(new JScrollPane(pendingBookingsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton approveButton = new JButton("قبول");
        approveButton.addActionListener(e -> updateBookingStatus("approved"));
        JButton rejectButton = new JButton("رفض");
        rejectButton.addActionListener(e -> updateBookingStatus("rejected"));

        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createAllBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columnNames = {"ID الحجز", "ID المستخدم", "ID القاعة", "التاريخ", "الغرض", "الحالة"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
             public boolean isCellEditable(int row, int column) { return false; }
        };
        allBookingsTable = new JTable(model);
        panel.add(new JScrollPane(allBookingsTable), BorderLayout.CENTER);

        // Button for exporting the report
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportButton = new JButton("تصدير تقرير");
        exportButton.addActionListener(e -> exportReport());
        buttonPanel.add(exportButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshAdminRoomsTable() {
        System.out.println("refreshing rooms...");
        if (roomsTable == null) return; // Defensive check in case the table isn't initialized

        DefaultTableModel model = (DefaultTableModel) roomsTable.getModel();
        model.setRowCount(0); // Clear current rows

        ArrayList<Room> rooms = DatabaseManager.getAvailableRooms(); // Fetch rooms from DB
        for (Room room : rooms) {
            model.addRow(new Object[] {
                room.getId(),
                room.getName(),
                room.getCapacity()
            });
        }
    }


    private void refreshPendingBookingsTable() {
        DefaultTableModel model = (DefaultTableModel) pendingBookingsTable.getModel();
        int selectedRow = pendingBookingsTable.getSelectedRow();
        model.setRowCount(0);
        ArrayList<Booking> bookings = DatabaseManager.getPendingBookings();
        for (Booking booking : bookings) {
            model.addRow(new Object[]{booking.getId(), booking.getUserId(), booking.getRoomId(), booking.getDate(), booking.getPurpose(), booking.getStatus()});
        }
        if (selectedRow >= 0 && selectedRow < pendingBookingsTable.getRowCount()) {
            pendingBookingsTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        // refresh the "All Bookings" view as statuses might change.
        refreshAllBookingsTable(); 
    }
    
    private void refreshAllBookingsTable() {
        if (allBookingsTable == null) return; // Defensive check

        DefaultTableModel model = (DefaultTableModel) allBookingsTable.getModel();
        model.setRowCount(0); // Clear current rows

        ArrayList<Booking> bookings = DatabaseManager.getAllBookings(); // Fetch all bookings
        for (Booking booking : bookings) {
            model.addRow(new Object[]{
                booking.getId(),
                booking.getUserId(),
                booking.getRoomId(),
                booking.getDate(),
                booking.getPurpose(),
                booking.getStatus()
            });
        }
    }

    
    /**
     * method that saves the content of the "All Bookings" table to a text file.
     */
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("حفظ التقرير");
        fileChooser.setSelectedFile(new File("bookings_report.txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter fw = new FileWriter(fileToSave);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                
                bw.write("--- تقرير الحجوزات ---\n");
                DefaultTableModel model = (DefaultTableModel) allBookingsTable.getModel();
                // Write header
                for (int i = 0; i < model.getColumnCount(); i++) {
                    bw.write(model.getColumnName(i) + "\t\t");
                }
                bw.newLine();
                bw.write("------------------------------------------------------------------------------------\n");

                // Write data
                for (int i = 0; i < model.getRowCount(); i++) {
                    for (int j = 0; j < model.getColumnCount(); j++) {
                        bw.write(model.getValueAt(i, j).toString() + "\t\t");
                    }
                    bw.newLine();
                }
                
                bw.write("--- نهاية التقرير ---\n");
                JOptionPane.showMessageDialog(this, "تم تصدير التقرير بنجاح إلى:\n" + fileToSave.getAbsolutePath(), "نجاح", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "خطأ أثناء حفظ الملف: " + ex.getMessage(), "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void updateBookingStatus(String status) {
        int selectedRow = pendingBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "الرجاء اختيار طلب من القائمة أولاً.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) pendingBookingsTable.getValueAt(selectedRow, 0);
        if (DatabaseManager.updateBookingStatus(bookingId, status)) {
            JOptionPane.showMessageDialog(this, "تم تحديث حالة الحجز بنجاح.");
            refreshPendingBookingsTable();
        } else {
            JOptionPane.showMessageDialog(this, "فشل تحديث حالة الحجز.", "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addRoom() {
        JTextField nameField = new JTextField();
        JTextField capacityField = new JTextField();

        JComponent[] inputs = new JComponent[] {
            new JLabel("اسم القاعة:"),
            nameField,
            new JLabel("السعة:"),
            capacityField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "إضافة قاعة جديدة", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                if (name.isEmpty() || capacity <= 0) {
                    throw new IllegalArgumentException();
                }

                Room room = new Room();
                room.setName(name);
                room.setCapacity(capacity);

                if (DatabaseManager.addRoom(room)) {
                    JOptionPane.showMessageDialog(this, "تمت إضافة القاعة بنجاح.");
                    refreshAdminRoomsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "فشل في إضافة القاعة.", "خطأ", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "الرجاء إدخال بيانات صحيحة.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void editRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "الرجاء اختيار قاعة للتعديل.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomId = (int) roomsTable.getValueAt(selectedRow, 0);
        String currentName = (String) roomsTable.getValueAt(selectedRow, 1);
        int currentCapacity = (int) roomsTable.getValueAt(selectedRow, 2);

        JTextField nameField = new JTextField(currentName);
        JTextField capacityField = new JTextField(String.valueOf(currentCapacity));

        JComponent[] inputs = new JComponent[] {
            new JLabel("اسم القاعة:"),
            nameField,
            new JLabel("السعة:"),
            capacityField
        };

        int result = JOptionPane.showConfirmDialog(this, inputs, "تعديل القاعة", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int capacity = Integer.parseInt(capacityField.getText().trim());

                if (DatabaseManager.updateRoom(roomId, name, capacity)) {
                    JOptionPane.showMessageDialog(this, "تم تعديل القاعة بنجاح.");
                    refreshAdminRoomsTable();
                } else {
                    JOptionPane.showMessageDialog(this, "فشل في تعديل القاعة.", "خطأ", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "الرجاء إدخال بيانات صحيحة.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "الرجاء اختيار قاعة للحذف.", "خطأ", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int roomId = (int) roomsTable.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "هل أنت متأكد أنك تريد حذف هذه القاعة؟", "تأكيد الحذف", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (DatabaseManager.deleteRoom(roomId)) {
                JOptionPane.showMessageDialog(this, "تم حذف القاعة بنجاح.");
                refreshAdminRoomsTable();
            } else {
                JOptionPane.showMessageDialog(this, "فشل في حذف القاعة.", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}

