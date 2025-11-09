package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.model.Order;
import com.example.finalproject.model.OrderItem;
import com.example.finalproject.model.Product;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.CartService;
import com.example.finalproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartController {

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private Label totalLabel;

    private final CartService cartService = CartService.getInstance();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        colPrice.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        colQty.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        colTotal.setCellValueFactory(data -> data.getValue().totalProperty().asObject());
        refreshTable();
    }

    private void refreshTable() {
        ObservableList<CartItem> items = FXCollections.observableArrayList();
        for (Map.Entry<Product, Integer> e : cartService.getItems().entrySet()) {
            items.add(new CartItem(e.getKey(), e.getValue()));
        }
        cartTable.setItems(items);
        totalLabel.setText(String.format("$%.2f", cartService.getTotal()));
    }

    @FXML
    private void onRemove() {
        CartItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartService.removeItem(selected.getProduct());
            refreshTable();
        } else {
            showAlert("No selection", "Please select an item to remove.");
        }
    }

    @FXML
    private void onCheckout() {
        if (cartService.getItems().isEmpty()) {
            showAlert("Cart is empty!", "Add some items first.");
            return;
        }
        HelloApplication.setRoot("view/checkout.fxml");
    }

    @FXML
    private void onBackToShop() {
        HelloApplication.setRoot("view/customer_home.fxml");
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
