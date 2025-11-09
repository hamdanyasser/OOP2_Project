package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.ProductDao;
import com.example.finalproject.dao.WishlistDao;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.ShoppingCart;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.ProductService;
import com.example.finalproject.service.CartService;
import com.example.finalproject.service.ReviewService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.finalproject.controller.ReviewPopupController;

import java.util.ArrayList;
import java.util.List;

public class CustomerHomeController {

    @FXML
    private FlowPane productGrid;

    private final ProductService productService = new ProductService();
    private final CartService cartService = CartService.getInstance();
    private final ProductDao productDao = new ProductDao();
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 14;
    private List<Product> allProducts;
    @FXML private Button prevBtn, nextBtn;
    @FXML private Label pageLabel;
    @FXML private TextField searchField;
    @FXML private ChoiceBox<String> categoryChoice;
    private List<Product> filteredProducts;

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        loadProducts();

        // Initialize categories (you can load dynamically if you prefer)
        categoryChoice.getItems().addAll("All", "Game", "Food", "Human", "Gaming");
        categoryChoice.setValue("All");

        // 🔍 LIVE SEARCH listener
        searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // 🧩 Update results when category changes
        categoryChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }


    @FXML
    private void applyFilters() {
        String keyword = searchField.getText().toLowerCase().trim();
        String category = categoryChoice.getValue();

        filteredProducts = allProducts.stream()
                .filter(p -> (category.equals("All") || p.getCategory().equalsIgnoreCase(category)))
                .filter(p -> p.getName().toLowerCase().contains(keyword)
                        || p.getCategory().toLowerCase().contains(keyword))
                .toList();

        currentPage = 1;
        showPage(currentPage, filteredProducts);
    }

    @FXML
    private void onReset() {
        searchField.clear();
        categoryChoice.setValue("All");
        applyFilters();
    }
    private void highlightSearch(Label label, String text, String keyword) {
        if (keyword.isEmpty()) return;
        if (text.toLowerCase().contains(keyword.toLowerCase())) {
            label.setStyle("-fx-font-weight: bold; -fx-text-fill: #0078D7;");
        } else {
            label.setStyle("-fx-text-fill: black;");
        }
    }

    private void loadProducts() {
        allProducts = productService.getAll();
        filteredProducts = new ArrayList<>(allProducts);
        showPage(currentPage, filteredProducts);
    }

    private void showPage(int page, List<Product> list) {
        int start = (page - 1) * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, list.size());

        productGrid.getChildren().clear();
        for (int i = start; i < end; i++) {
            Product p = list.get(i);
            VBox card = createProductCard(p);
            productGrid.getChildren().add(card);
        }

        pageLabel.setText("Page " + currentPage + " / " + Math.max(1, (int)Math.ceil(list.size() / (double)ITEMS_PER_PAGE)));
        prevBtn.setDisable(currentPage == 1);
        nextBtn.setDisable(end >= list.size());
    }

    @FXML
    private void onNextPage() {
        if (currentPage * ITEMS_PER_PAGE < filteredProducts.size()) {
            currentPage++;
            showPage(currentPage, filteredProducts);
        }
    }

    @FXML
    private void onPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage, filteredProducts);
        }
    }


    private VBox createProductCard(Product p) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                + "-fx-border-color: #ddd; -fx-border-radius: 8; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);");
        card.setPrefWidth(180);

        // 🖼️ Product image
        ImageView imageView = new ImageView();
        if (p.getImagePath() != null && !p.getImagePath().isEmpty()) {
            imageView.setImage(new Image("file:" + p.getImagePath(), 120, 120, true, true));
        } else {
            imageView.setImage(new Image(getClass().getResource("/com/example/finalproject/view/default.png").toExternalForm()));
        }
        imageView.setFitWidth(120);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);

        // 🏷️ Labels
        Label nameLabel = new Label(p.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");
        highlightSearch(nameLabel, p.getName(), searchField.getText());

        Label categoryLabel = new Label(p.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");

        // 💸 Price & Discount
        double originalPrice = p.getPrice();
        double discount = p.getDiscount();
        double finalPrice = originalPrice * (1 - discount / 100.0);

        Label priceLabel = new Label("$" + String.format("%.2f", finalPrice));
        priceLabel.setStyle("-fx-text-fill: #0078D7; -fx-font-weight: bold; -fx-font-size: 14;");

        VBox priceBox = new VBox(2);
        priceBox.setAlignment(Pos.CENTER);

        if (discount > 0) {
            Label oldPrice = new Label("$" + String.format("%.2f", originalPrice));
            oldPrice.setStyle("-fx-text-fill: gray; -fx-font-size: 11; -fx-strikethrough: true;");

            Label discountLabel = new Label("-" + (int) discount + "%");
            discountLabel.setStyle("-fx-background-color: red; -fx-text-fill: white; "
                    + "-fx-padding: 2 6 2 6; -fx-background-radius: 8; -fx-font-size: 11;");

            priceBox.getChildren().addAll(priceLabel, oldPrice, discountLabel);
        } else {
            priceBox.getChildren().add(priceLabel);
        }

        // 📦 Stock
        Label stockLabel = new Label("Stock: " + p.getStock());
        stockLabel.setStyle("-fx-font-size: 12; -fx-text-fill: gray;");

        // ⭐ Rating
        Label ratingLabel = new Label("⭐ " + String.format("%.1f", new ReviewService().getAverageRating(p.getId())));
        ratingLabel.setStyle("-fx-text-fill: #f5b301; -fx-font-weight:bold;");

        // 🛒 Add to Cart
        Button addBtn = new Button("Add to Cart");
        addBtn.setOnAction(e -> {
            if (p.getStock() <= 0) {
                showAlert("❌ Out of Stock", p.getName() + " is currently unavailable.");
                return;
            }

            cartService.addItem(p);
            ShoppingCart.addItem(p.getId(), finalPrice);

            productDao.decreaseStock(p.getId(), 1);
            p.setStock(p.getStock() - 1);
            stockLabel.setText("Stock: " + p.getStock());

            showAlert("🛒 Added to Cart", p.getName() + " added to your cart!");
        });

        // ❤️ Wishlist button
        WishlistDao wishlistDao = new WishlistDao();
        int userId = Session.getUserId();
        boolean inWishlist = wishlistDao.isInWishlist(userId, p.getId());

        Button wishlistBtn = new Button(inWishlist ? "❤️ Remove from Wishlist" : "🤍 Add to Wishlist");
        wishlistBtn.setOnAction(e -> {
            if (wishlistDao.isInWishlist(userId, p.getId())) {
                wishlistDao.removeFromWishlist(userId, p.getId());
                wishlistBtn.setText("🤍 Add to Wishlist");
            } else {
                wishlistDao.addToWishlist(userId, p.getId());
                wishlistBtn.setText("❤️ Remove from Wishlist");
            }
        });

        // ✍ Leave Review
        Button reviewBtn = new Button("Leave Review");
        reviewBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/finalproject/view/review_popup.fxml"));
                VBox root = loader.load();

                ReviewPopupController controller = loader.getController();
                controller.setProduct(p.getId(), p.getName());

                Stage stage = new Stage();
                stage.setTitle("Review " + p.getName());
                stage.setScene(new Scene(root));
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.showAndWait();

                loadProducts();
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Error", "Failed to open review popup: " + ex.getMessage());
            }
        });

        // 👀 View Reviews
        Button viewReviewsBtn = new Button("View Reviews");
        viewReviewsBtn.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/finalproject/view/product_reviews.fxml"));
                VBox root = loader.load();

                ProductReviewsController controller = loader.getController();
                controller.setProduct(p.getId(), p.getName());

                Stage stage = new Stage();
                stage.setTitle("Reviews for " + p.getName());
                stage.setScene(new Scene(root));
                stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // 🧱 Final Layout
        card.getChildren().addAll(
                imageView,
                nameLabel,
                categoryLabel,
                priceBox,
                ratingLabel,
                stockLabel,
                addBtn,
                wishlistBtn,   // ❤️ Added here
                reviewBtn,
                viewReviewsBtn
        );

        return card;
    }


    @FXML
    private void onWishlist() {
        HelloApplication.setRoot("view/wishlist.fxml");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onProfile() {
        HelloApplication.setRoot("view/profile_settings.fxml");
    }

    @FXML
    private void onViewOrders() {
        HelloApplication.setRoot("view/order_history.fxml");
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }

    @FXML
    private void onViewCart() {
        HelloApplication.setRoot("view/cart.fxml");
    }
}
