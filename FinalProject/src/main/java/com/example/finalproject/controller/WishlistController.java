package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.WishlistDao;
import com.example.finalproject.model.Product;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

import java.util.List;

public class WishlistController {
    @FXML private FlowPane wishlistGrid;
    private final WishlistDao dao = new WishlistDao();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        loadWishlist();
    }

    private void loadWishlist() {
        wishlistGrid.getChildren().clear();
        List<Product> products = dao.getUserWishlist(Session.getUserId());

        for (Product p : products) {
            VBox card = new VBox(8);
            card.setAlignment(Pos.CENTER);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color:white; -fx-border-color:#ddd; -fx-background-radius:8;");
            card.setPrefWidth(180);

            ImageView image = new ImageView(new Image("file:" + p.getImagePath(), 100, 100, true, true));
            Label name = new Label(p.getName());
            name.setStyle("-fx-font-weight:bold;");
            Label price = new Label("$" + p.getPrice());
            Button remove = new Button("🗑 Remove");
            remove.setOnAction(e -> {
                dao.removeFromWishlist(Session.getUserId(), p.getId());
                loadWishlist();
            });

            card.getChildren().addAll(image, name, price, remove);
            wishlistGrid.getChildren().add(card);
        }
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/customer_home.fxml");
    }
}
