package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.model.Product;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.List;

public class AdminProductsController {

    @FXML private TableView<Product> table;
    @FXML private TableColumn<Product, Integer> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, String> colCategory;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;
    @FXML private Label msgLabel;
    @FXML private TableColumn<Product, String> colImage;

    private final ProductService productService = new ProductService();
    private final ObservableList<Product> productList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // ✅ Initialize table columns
        AuthGuard.requireLogin();
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colCategory.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCategory()));
        colPrice.setCellValueFactory(data -> new javafx.beans.property.SimpleDoubleProperty(data.getValue().getPrice()).asObject());
        colStock.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getStock()).asObject());
        colImage.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getImagePath()));
        colImage.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String path, boolean empty) {
                super.updateItem(path, empty);
                if (empty || path == null || path.isEmpty()) {
                    setGraphic(null);
                } else {
                    imageView.setFitWidth(60);
                    imageView.setFitHeight(60);
                    imageView.setPreserveRatio(true);
                    imageView.setImage(new javafx.scene.image.Image("file:" + path, 60, 60, true, true));
                    setGraphic(imageView);
                }
            }
        });
        table.setItems(productList);
        refresh();
    }

    // ✅ Load all products
    private void refresh() {
        List<Product> products = productService.getAll();
        productList.setAll(products);
        msgLabel.setText("");
    }

    // ✅ Add button
    @FXML
    private void onAdd() {
        openProductForm(null);
    }

    // ✅ Edit button
    @FXML
    private void onEdit() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            msgLabel.setText("⚠ Select a product to edit.");
            return;
        }
        openProductForm(selected);
    }

    // ✅ Delete button
    @FXML
    private void onDelete() {
        Product selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            msgLabel.setText("⚠ Select a product to delete.");
            return;
        }

        try {
            productService.delete(selected.getId());
            refresh();
            msgLabel.setStyle("-fx-text-fill: green;");
            msgLabel.setText("✅ Product deleted successfully!");
        } catch (Exception e) {
            msgLabel.setStyle("-fx-text-fill: red;");
            msgLabel.setText("❌ " + e.getMessage());
        }
    }

    // ✅ Open add/edit form
    private void openProductForm(Product product) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/finalproject/view/product_form.fxml"));
            Parent root = loader.load();

            ProductFormController controller = loader.getController();
            controller.setProduct(product);
            controller.setOnSaveCallback(this::refresh);

            Stage stage = new Stage();
            stage.setTitle(product == null ? "Add Product" : "Edit Product");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onManageReviews() {
        try {
            com.example.finalproject.HelloApplication.setRoot("view/admin_reviews.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOrders() {
        try {
            com.example.finalproject.HelloApplication.setRoot("view/admin_orders.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onPromotions() {
        try {
            HelloApplication.setRoot("view/admin_promotions.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ✅ Logout
    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }
}
