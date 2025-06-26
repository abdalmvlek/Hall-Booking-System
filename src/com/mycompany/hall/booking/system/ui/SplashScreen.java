// SplashScreen.java
// A simple splash screen that appears when the application starts.
package com.mycompany.hall.booking.system.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SplashScreen extends JFrame {

    public SplashScreen() {
        // Frame setup
        setUndecorated(true); // Remove window border
        setSize(450, 300);
        setLocationRelativeTo(null); // Center the screen
        setLayout(new BorderLayout());
        
        // Main panel
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(33, 37, 41)); // Dark background
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(panel, BorderLayout.CENTER);

        // Title Label
        JLabel titleLabel = new JLabel("Hall Booking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Icon/Image (Optional)
        // You can add an icon here if you have one. For now, a simple text.
        JLabel iconLabel = new JLabel("🚀", SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 100));
        panel.add(iconLabel, BorderLayout.CENTER);

        // Loading text and progress bar
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setOpaque(false);
        JLabel loadingLabel = new JLabel("Loading, please wait...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        loadingLabel.setForeground(Color.LIGHT_GRAY);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true); // Indefinite loading animation
        progressBar.setStringPainted(false);

        southPanel.add(loadingLabel, BorderLayout.NORTH);
        southPanel.add(progressBar, BorderLayout.SOUTH);
        panel.add(southPanel, BorderLayout.SOUTH);
    }
}

