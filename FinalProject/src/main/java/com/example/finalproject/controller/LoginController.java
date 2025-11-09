package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label msgLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void onLogin() {
        try {
            String token = authService.login(emailField.getText().trim(), passwordField.getText().trim());
            Session.setToken(token);
            String role = JwtService.getRole(token);


            if ("ADMIN".equals(role))
                HelloApplication.setRoot("view/admin_products.fxml");
            else
                HelloApplication.setRoot("view/customer_home.fxml");
        } catch (Exception e) {
            msgLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onForgotPassword() {
        HelloApplication.setRoot("view/forgot_password.fxml");
    }

    @FXML
    public void goRegister() {
        HelloApplication.setRoot("view/register.fxml");
    }
}
