package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.DBConnection;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class ProfileSettingsController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private PasswordField oldPassField;
    @FXML private PasswordField newPassField;
    @FXML private Label msgLabel;

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        loadUserData();
    }

    private void loadUserData() {
        try {
            int userId = JwtService.getUserId(Session.getToken());
            Connection conn = DBConnection.getInstance();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT name, email, address FROM users WHERE id=?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                addressField.setText(rs.getString("address"));
            }
        } catch (Exception e) {
            msgLabel.setText("⚠️ Error loading profile data.");
            e.printStackTrace();
        }
    }

    @FXML
    private void onSave() {
        try {
            int userId = JwtService.getUserId(Session.getToken());
            Connection conn = DBConnection.getInstance();

            // --- Fetch user and verify old password ---
            PreparedStatement check = conn.prepareStatement("SELECT password_hash FROM users WHERE id=?");
            check.setInt(1, userId);
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                msgLabel.setText("❌ User not found.");
                return;
            }

            String oldHash = rs.getString("password_hash");
            if (!BCrypt.checkpw(oldPassField.getText(), oldHash)) {
                msgLabel.setText("❌ Incorrect old password.");
                return;
            }

            // --- Get form values ---
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            String newPass = newPassField.getText().trim();

            // ---------- VALIDATION ----------
            if (name.isEmpty() || email.isEmpty() || address.isEmpty()) {
                msgLabel.setText("⚠️ All fields are required.");
                return;
            }

            // ✅ Email format check
            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                msgLabel.setText("⚠️ Invalid email format.");
                return;
            }

            // ✅ Password strength (only if changing password)
            if (!newPass.isEmpty()) {
                if (newPass.length() < 8
                        || !newPass.matches(".*[A-Z].*")
                        || !newPass.matches(".*[a-z].*")
                        || !newPass.matches(".*\\d.*")
                        || !newPass.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
                    msgLabel.setText("⚠️ Password must have upper, lower, number & special char.");
                    return;
                }
            }

            // --- Update query ---
            String sql;
            PreparedStatement ps;
            if (newPass.isEmpty()) {
                sql = "UPDATE users SET name=?, email=?, address=? WHERE id=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, address);
                ps.setInt(4, userId);
            } else {
                sql = "UPDATE users SET name=?, email=?, address=?, password_hash=? WHERE id=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, name);
                ps.setString(2, email);
                ps.setString(3, address);
                ps.setString(4, BCrypt.hashpw(newPass, BCrypt.gensalt()));
                ps.setInt(5, userId);
            }

            int updated = ps.executeUpdate();
            if (updated > 0) {
                msgLabel.setStyle("-fx-text-fill:green;");
                msgLabel.setText("✅ Profile updated successfully!");
            } else {
                msgLabel.setStyle("-fx-text-fill:red;");
                msgLabel.setText("⚠️ No changes made.");
            }

        } catch (Exception e) {
            msgLabel.setStyle("-fx-text-fill:red;");
            msgLabel.setText("⚠️ Error saving changes.");
            e.printStackTrace();
        }
    }


    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/customer_home.fxml");
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }
}
