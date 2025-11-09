package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.ReviewDao;
import com.example.finalproject.model.Review;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AdminReviewsController {

    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> colUser;
    @FXML private TableColumn<Review, String> colProduct;
    @FXML private TableColumn<Review, Number> colRating;
    @FXML private TableColumn<Review, String> colComment;
    @FXML private TableColumn<Review, String> colDate;
    @FXML private TextField searchField;

    private final ReviewDao dao = new ReviewDao();
    private ObservableList<Review> reviewList;

    @FXML
    public void initialize() {
        // --- Setup table columns ---
        AuthGuard.requireLogin();
        colUser.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));
        colProduct.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getProductName()));
        colRating.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getRating()));
        colComment.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getComment()));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCreatedAt() != null ? data.getValue().getCreatedAt().toString() : ""));

        // --- Load data ---
        List<Review> reviews = dao.getAllReviewsForAdmin();
        reviewList = FXCollections.observableArrayList(reviews);

        // --- Enable search filtering ---
        FilteredList<Review> filteredList = new FilteredList<>(reviewList, b -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filter = newVal.toLowerCase().trim();
            filteredList.setPredicate(r -> {
                if (filter.isEmpty()) return true;
                return r.getUsername().toLowerCase().contains(filter)
                        || r.getProductName().toLowerCase().contains(filter)
                        || r.getComment().toLowerCase().contains(filter)
                        || String.valueOf(r.getRating()).contains(filter);
            });
        });

        SortedList<Review> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(reviewTable.comparatorProperty());
        reviewTable.setItems(sortedList);
    }

    @FXML
    private void onDelete() {
        Review selected = reviewTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select a review to delete.");
            return;
        }

        try {
            java.sql.Connection conn = com.example.finalproject.dao.DBConnection.getInstance();
            java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM review WHERE id=?");
            ps.setInt(1, selected.getId());
            ps.executeUpdate();

            reviewList.remove(selected);
            showAlert("Review deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error deleting review: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/admin_products.fxml");
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
