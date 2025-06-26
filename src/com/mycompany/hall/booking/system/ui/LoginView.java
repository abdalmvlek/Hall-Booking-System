// LoginView.java
// Handles user login.
package com.mycompany.hall.booking.system.ui;

import com.mycompany.hall.booking.system.DatabaseManager;
import com.mycompany.hall.booking.system.User;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class LoginView extends JFrame {

    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public LoginView() {
        // Frame setup
        setTitle("Login - Hall Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        applyComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT); 


        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(73, 80, 87));
        JLabel titleLabel = new JLabel("مرحبا مجددا");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
        formPanel.setBackground(Color.WHITE);

        // Email
        formPanel.add(createLabel("البريد الالكتروني"));
        emailField = createTextField();
        formPanel.add(emailField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Password
        formPanel.add(createLabel("كلمة السر"));
        passwordField = createPasswordField();
        formPanel.add(passwordField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login Button
        JButton loginButton = createButton("تسجيل الدخول");
        loginButton.addActionListener(e -> handleLogin());
        formPanel.add(loginButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(errorLabel);


        // Sign Up Link
        JLabel signUpLabel = new JLabel("<html>ليس لديك حساب؟   <b>سجل</b></html>");
        signUpLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        signUpLabel.setForeground(new Color(0, 123, 255));
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signUpLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new SignUpView().setVisible(true);
                dispose();
            }
        });
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(signUpLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }
    
    private void handleLogin() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if(email.isEmpty() || password.isEmpty()){
            errorLabel.setText("يرجى تعبئ جميع الحقول المطلوبة.");
            return;
        }

        User user = DatabaseManager.validateLogin(email, password);

        if (user != null) {
            // Login successful
            dispose(); // Close login window
            if ("admin".equalsIgnoreCase(user.getRole())) {
                new AdminDashboard().setVisible(true);
            } else {
                new UserDashboard(user).setVisible(true);
            }
        } else {
            // Login failed
            errorLabel.setText("البريد الالكتروني او كلمة السر غير صحيحة.");
        }
    }

    // Helper methods for creating styled components
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.RIGHT_ALIGNMENT);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textField.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, textField.getPreferredSize().height));
        return textField;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pwField = new JPasswordField(20);
        pwField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pwField.setBorder(new CompoundBorder(new LineBorder(Color.GRAY), new EmptyBorder(5, 5, 5, 5)));
        pwField.setMaximumSize(new Dimension(Integer.MAX_VALUE, pwField.getPreferredSize().height));
        return pwField;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(0, 123, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }
}

