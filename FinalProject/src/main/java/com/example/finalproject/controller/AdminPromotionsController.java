package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.PromotionDao;
import com.example.finalproject.model.Product;
import com.example.finalproject.model.Promotion;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.service.ProductService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class AdminPromotionsController {

    @FXML private TableView<Promotion> table;
    @FXML private TableColumn<Promotion, Integer> colId;
    @FXML private TableColumn<Promotion, Integer> colProductId;
    @FXML private TableColumn<Promotion, String> colCategory;
    @FXML private TableColumn<Promotion, Double> colDiscount;
    @FXML private TableColumn<Promotion, String> colStart;
    @FXML private TableColumn<Promotion, String> colEnd;
    @FXML private Label msgLabel;

    private final PromotionDao dao = new PromotionDao();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        colId.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colProductId.setCellValueFactory(data -> new SimpleIntegerProperty(
                data.getValue().getProductId() != null ? data.getValue().getProductId() : 0
        ).asObject());
        colCategory.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCategory()));
        colDiscount.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getDiscount()).asObject());
        colStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStartDate().toString()));
        colEnd.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEndDate().toString()));

        refresh();
    }

    private void refresh() {
        List<Promotion> list = dao.getAll();
        table.getItems().setAll(list);
    }

    @FXML
    private void onAdd() {
        Dialog<Promotion> dialog = new Dialog<>();
        dialog.setTitle("Add Promotion");
        dialog.setHeaderText("Fill in the promotion details:");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // 🧱 Fields
        ComboBox<Product> productCombo = new ComboBox<>();
        productCombo.getItems().addAll(new ProductService().getAll());
        productCombo.setPromptText("Select Product (optional)");
        productCombo.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getName() + " (#" + p.getId() + ")");
            }
        });
        productCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Product p, boolean empty) {
                super.updateItem(p, empty);
                setText(empty || p == null ? "" : p.getName());
            }
        });

        TextField categoryField = new TextField();
        categoryField.setPromptText("Category (optional)");

        Spinner<Double> discountSpinner = new Spinner<>();
        SpinnerValueFactory<Double> valueFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 100.0, 10.0, 1.0);
        discountSpinner.setValueFactory(valueFactory);
        discountSpinner.setEditable(true);



        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Product:"), 0, 0);
        grid.add(productCombo, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(new Label("Discount (%):"), 0, 2);
        grid.add(discountSpinner, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDatePicker, 1, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDatePicker, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // ✅ Convert the result
        dialog.setResultConverter(button -> {
            if (button == addButtonType) {
                try {
                    // --- Validate fields ---
                    if (discountSpinner.getValue() <= 0) {
                        showError("Discount must be greater than 0%");
                        return null;
                    }
                    if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                        showError("Please select both start and end dates.");
                        return null;
                    }
                    if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
                        showError("End date must be after the start date.");
                        return null;
                    }

                    // --- Build promotion ---
                    Promotion p = new Promotion();
                    Product selected = productCombo.getValue();
                    if (selected != null) p.setProductId(selected.getId());
                    p.setCategory(categoryField.getText().isBlank() ? null : categoryField.getText());
                    p.setDiscount(discountSpinner.getValue());
                    p.setStartDate(Date.valueOf(startDatePicker.getValue()));
                    p.setEndDate(Date.valueOf(endDatePicker.getValue()));
                    return p;

                } catch (Exception ex) {
                    showError("Invalid input: " + ex.getMessage());
                }
            }
            return null;
        });


        // ✅ Handle result
        dialog.showAndWait().ifPresent(promo -> {
            try {
                dao.insert(promo);
                refresh();
                msgLabel.setStyle("-fx-text-fill: green;");
                msgLabel.setText("✅ Promotion added successfully!");
            } catch (Exception e) {
                msgLabel.setStyle("-fx-text-fill: red;");
                msgLabel.setText("❌ Failed: " + e.getMessage());
            }
        });
    }


    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void onDelete() {
        Promotion selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            msgLabel.setText("⚠ Select a promotion to delete.");
            return;
        }
        dao.delete(selected.getId());
        refresh();
        msgLabel.setStyle("-fx-text-fill: green;");
        msgLabel.setText("✅ Promotion deleted!");
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/admin_products.fxml");
    }
}
