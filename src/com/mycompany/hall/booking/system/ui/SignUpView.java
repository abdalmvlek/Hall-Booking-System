// SignUpView.java
// Handles new user registration.
package com.mycompany.hall.booking.system.ui;

import com.mycompany.hall.booking.system.DatabaseManager;
import com.mycompany.hall.booking.system.User;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class SignUpView extends JFrame {

    private final JTextField nameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final JLabel errorLabel;

    public SignUpView() {
        // Frame setup
        setTitle("Sign Up - Hall Booking System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(40, 167, 69));
        JLabel titleLabel = new JLabel("انشاء حساب جديد");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 50, 40, 50));
        formPanel.setBackground(Color.WHITE);

        // Name
        formPanel.add(createLabel("الاسم الكامل"));
        nameField = createTextField();
        formPanel.add(nameField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

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

        // Sign Up Button
        JButton signUpButton = createButton("سجل الأن");
        signUpButton.addActionListener(e -> handleSignUp());
        formPanel.add(signUpButton);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Error Label
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(errorLabel);

        // Login Link
        JLabel loginLabel = new JLabel("<html>لديك حساب بالفعل؟ <b>سجل دخولك</b></html>");
        loginLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        loginLabel.setForeground(new Color(0, 123, 255));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new LoginView().setVisible(true);
                dispose();
            }
        });
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(loginLabel);

        mainPanel.add(formPanel, BorderLayout.CENTER);
    }

    private void handleSignUp() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        
        if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
            errorLabel.setText("قم بتعبئ جميع الحقول المطلوبة.");
            return;
        }

        // Check if user already exists
        if(DatabaseManager.getUserByEmail(email) != null){
            errorLabel.setText("البريد الالكتروني مستعمل حاول مرة اخرى.");
            return;
        }

        User newUser = new User(0, name, email, password, "user"); // Default role is "user"
        boolean success = DatabaseManager.registerUser(newUser);

        if (success) {
            JOptionPane.showMessageDialog(this, "تمت العملية بنجاح، سجل دخولك.", "نجاح", JOptionPane.INFORMATION_MESSAGE);
            new LoginView().setVisible(true);
            dispose();
        } else {
            errorLabel.setText("حدث خطأ ما يرجى إعادة المحاولة.");
        }
    }
    
    // Helper methods for creating styled components
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
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
        button.setBackground(new Color(40, 167, 69));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }
}

