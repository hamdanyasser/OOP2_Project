package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField nameField, emailField, addressField;
    @FXML private PasswordField passwordField;
    @FXML private Label msgLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void onRegister() {
        // clear previous message
        msgLabel.setText("");

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String address = addressField.getText().trim();

        // ---------- VALIDATION ----------
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty()) {
            msgLabel.setText("All fields are required.");
            return;
        }

        // ✅ Email format check
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            msgLabel.setText("Invalid email format.");
            return;
        }

        // ✅ Password strength: 8+, upper, lower, number, special
        if (password.length() < 8
                || !password.matches(".*[A-Z].*")
                || !password.matches(".*[a-z].*")
                || !password.matches(".*\\d.*")
                || !password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            msgLabel.setText("Password must have upper, lower, number & special char.");
            return;
        }

        try {
            // ✅ Call AuthService (it throws if email already exists)
            String token = authService.register(name, email, password, address);

            msgLabel.setStyle("-fx-text-fill: green;");
            msgLabel.setText("Registration successful! Redirecting...");

            Session.setToken(token);
            HelloApplication.setRoot("view/customer_home.fxml");

        } catch (Exception e) {
            msgLabel.setStyle("-fx-text-fill: red;");
            msgLabel.setText(e.getMessage());
        }
    }

    @FXML
    public void goLogin() {
        HelloApplication.setRoot("view/login.fxml");
    }
}
