package com.example.finalproject.controller;

import com.example.finalproject.dao.UserDao;
import com.example.finalproject.util.EmailSender;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Random;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private TextField otpField;
    @FXML private PasswordField newPasswordField;
    @FXML private Label messageLabel;

    private String generatedOtp;
    private String targetEmail;

    @FXML
    private void onSendOtp() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            messageLabel.setText("Enter your email.");
            return;
        }

        UserDao dao = new UserDao();
        if (!dao.emailExists(email)) {
            messageLabel.setText("No account found with that email.");
            return;
        }

        generatedOtp = String.valueOf(new Random().nextInt(900000) + 100000);
        targetEmail = email;

        boolean sent = EmailSender.sendEmail(email, "Password Reset OTP",
                "Your OTP code is: " + generatedOtp);

        messageLabel.setText(sent ? "OTP sent to your email." : "Failed to send email.");
    }

    @FXML
    private void onResetPassword() {
        if (!otpField.getText().equals(generatedOtp)) {
            messageLabel.setText("Invalid OTP.");
            return;
        }

        String newPass = newPasswordField.getText();
        if (newPass.length() < 6) {
            messageLabel.setText("Password too short.");
            return;
        }

        new UserDao().updatePassword(targetEmail, newPass);
        messageLabel.setText("Password reset successfully!");
    }
}
