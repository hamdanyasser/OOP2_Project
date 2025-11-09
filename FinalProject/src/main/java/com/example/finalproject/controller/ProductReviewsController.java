package com.example.finalproject.controller;

import com.example.finalproject.dao.ReviewDao;
import com.example.finalproject.model.Review;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProductReviewsController {

    @FXML private Label productTitle;
    @FXML private TableView<Review> reviewTable;
    @FXML private TableColumn<Review, String> colUser;
    @FXML private TableColumn<Review, Number> colRating;
    @FXML private TableColumn<Review, String> colComment;
    @FXML private TableColumn<Review, String> colDate;

    private final ReviewDao dao = new ReviewDao();

    public void setProduct(int productId, String productName) {
        productTitle.setText("Reviews for " + productName);

        colUser.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getUsername()));
        colRating.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getRating()));
        colComment.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getComment()));
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt().toString()));

        reviewTable.setItems(FXCollections.observableArrayList(dao.getReviewsByProductWithUser(productId)));
    }
}
