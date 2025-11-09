package com.example.finalproject.controller;

import com.example.finalproject.model.Review;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.ReviewService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ReviewPopupController {
    @FXML private Label productLabel;
    @FXML private ChoiceBox<Integer> ratingChoice;
    @FXML private TextArea commentField;

    private final ReviewService service = new ReviewService();
    private int productId;

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        ratingChoice.getItems().addAll(1, 2, 3, 4, 5);
        ratingChoice.setValue(5);
    }

    public void setProduct(int id, String name) {
        this.productId = id;
        productLabel.setText("Reviewing: " + name);
    }

    @FXML
    private void onSubmit() {
        try {
            int rating = ratingChoice.getValue();
            String comment = commentField.getText();
            int userId = JwtService.getUserId(Session.getToken());

            Review r = new Review(0, productId, userId, rating, comment, null);
            service.addReview(r);

            showAlert("✅ Review added successfully!");
            ((Stage) productLabel.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Failed to submit review: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(msg);
        a.showAndWait();
    }
}
