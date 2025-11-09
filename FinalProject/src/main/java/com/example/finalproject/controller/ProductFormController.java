package com.example.finalproject.controller;

import com.example.finalproject.model.Product;
import com.example.finalproject.service.ProductService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ProductFormController {

    @FXML private Label formTitle;
    @FXML private TextField nameField, categoryField, priceField, stockField;
    @FXML private TextArea descriptionArea;
    @FXML private Label msgLabel;
    @FXML private ImageView imageView;

    private final ProductService productService = new ProductService();
    private Product currentProduct;
    private Runnable onSaveCallback;

    private String selectedImagePath;

    public void setProduct(Product product) {
        this.currentProduct = product;

        if (product != null) {
            formTitle.setText("Edit Product");
            nameField.setText(product.getName());
            categoryField.setText(product.getCategory());
            priceField.setText(String.valueOf(product.getPrice()));
            descriptionArea.setText(product.getDescription());
            stockField.setText(String.valueOf(product.getStock()));
            selectedImagePath = product.getImagePath();

            if (selectedImagePath != null && !selectedImagePath.isEmpty()) {
                imageView.setImage(new Image("file:" + selectedImagePath));
            }
        } else {
            formTitle.setText("Add Product");
        }
    }

    public void setOnSaveCallback(Runnable callback) {
        this.onSaveCallback = callback;
    }

    // ✅ Choose image
    @FXML
    private void onChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Ensure uploads directory exists
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) uploadsDir.mkdirs();

                // Copy image to uploads folder
                Path destination = Path.of("uploads", file.getName());
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                selectedImagePath = destination.toAbsolutePath().toString();
                imageView.setImage(new Image("file:" + selectedImagePath));
                msgLabel.setText("✅ Image selected!");

            } catch (Exception e) {
                msgLabel.setText("❌ Error copying image: " + e.getMessage());
            }
        }
    }

    // ✅ Save button
    @FXML
    private void onSave() {
        try {
            String name = nameField.getText().trim();
            String category = categoryField.getText().trim();
            String desc = descriptionArea.getText().trim();
            double price = Double.parseDouble(priceField.getText().trim());
            int stock = Integer.parseInt(stockField.getText().trim());

            if (currentProduct == null) {
                Product newP = new Product(0, name, category, price, desc, selectedImagePath, stock);
                productService.add(newP);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("✅ Product added!");
            } else {
                currentProduct.setName(name);
                currentProduct.setCategory(category);
                currentProduct.setDescription(desc);
                currentProduct.setPrice(price);
                currentProduct.setStock(stock);
                currentProduct.setImagePath(selectedImagePath);
                productService.update(currentProduct);
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("✅ Product updated!");
            }

            if (onSaveCallback != null) onSaveCallback.run();

            ((Stage) nameField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            msgLabel.setText("Invalid number in price or stock.");
        } catch (Exception e) {
            msgLabel.setText("❌ " + e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) nameField.getScene().getWindow()).close();
    }
}
