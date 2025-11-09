package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.OrderDao;
import com.example.finalproject.model.Order;
import com.example.finalproject.model.OrderItem;
import com.example.finalproject.model.Product;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.CartService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckoutController {

    @FXML
    private Label totalLabel;

    private final CartService cartService = CartService.getInstance();
    private final OrderDao orderDao = new OrderDao();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        double total = cartService.getTotal();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void onConfirmOrder() {
        try {
            int userId = JwtService.getUserId(Session.getToken());
            Map<Product, Integer> cartItems = cartService.getItems();

            if (cartItems.isEmpty()) {
                showAlert("Your cart is empty!");
                return;
            }

            // Prepare order items
            // CheckoutController.onConfirmOrder()
            List<OrderItem> orderItems = new ArrayList<>();
            for (var entry : cartItems.entrySet()) {
                Product p = entry.getKey();
                int qty = entry.getValue();
                double unit = p.getEffectivePrice();        // 🔸 use discounted price
                orderItems.add(new OrderItem(0, 0, p.getId(), qty, unit));
            }

            Order order = new Order();
            order.setUserId(userId);
            order.setItems(orderItems);
            order.setTotal(cartService.getTotal());          // 🔸 now discounted
            order.setStatus("PENDING");
            orderDao.saveOrder(order);


            // Clear cart
            cartService.clear();

            showAlert("✅ Order placed successfully!");
            HelloApplication.setRoot("view/customer_home.fxml");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("❌ Error saving order: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/cart.fxml");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
