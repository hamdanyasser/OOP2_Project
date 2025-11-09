package com.example.finalproject.controller;

import com.example.finalproject.dao.DBConnection;
import com.example.finalproject.model.OrderItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemsPopupController {

    @FXML private Label orderTitle;
    @FXML private TableView<OrderItem> itemTable;
    @FXML private TableColumn<OrderItem, String> colProduct;
    @FXML private TableColumn<OrderItem, Integer> colQty;
    @FXML private TableColumn<OrderItem, Double> colPrice;
    @FXML private TableColumn<OrderItem, Double> colSubtotal;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    private int orderId;

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        orderTitle.setText("Order #" + orderId + " - Items");
        loadItemsAndSummary();
    }

    private void loadItemsAndSummary() {
        List<OrderItem> items = new ArrayList<>();
        double total = 0.0;
        String status = "";

        try (Connection conn = DBConnection.getInstance()) {
            // 🧾 Get order details
            PreparedStatement psOrder = conn.prepareStatement("SELECT total, status FROM orders WHERE id=?");
            psOrder.setInt(1, orderId);
            ResultSet rsOrder = psOrder.executeQuery();
            if (rsOrder.next()) {
                total = rsOrder.getDouble("total");
                status = rsOrder.getString("status");
            }

            // 🛍 Get order items
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT oi.*, p.name FROM order_items oi JOIN products p ON oi.product_id = p.id WHERE oi.order_id = ?");
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("product_id"),
                        rs.getInt("quantity"),
                        rs.getDouble("price")
                );
                item.setProductName(rs.getString("name"));
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 🧮 Populate table
        ObservableList<OrderItem> data = FXCollections.observableArrayList(items);
        colProduct.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProductName()));
        colQty.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantity()).asObject());
        colPrice.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getPrice()).asObject());
        colSubtotal.setCellValueFactory(c -> new SimpleDoubleProperty(
                c.getValue().getPrice() * c.getValue().getQuantity()).asObject());

        itemTable.setItems(data);

        // 🧾 Update summary
        totalLabel.setText(String.format("$%.2f", total));
        statusLabel.setText(status);
        if ("DELIVERED".equalsIgnoreCase(status)) {
            statusLabel.setStyle("-fx-text-fill:green; -fx-font-weight:bold;");
        } else if ("PENDING".equalsIgnoreCase(status)) {
            statusLabel.setStyle("-fx-text-fill:orange; -fx-font-weight:bold;");
        } else {
            statusLabel.setStyle("-fx-text-fill:red; -fx-font-weight:bold;");
        }
    }

    @FXML
    private void onClose() {
        Stage stage = (Stage) orderTitle.getScene().getWindow();
        stage.close();
    }
}
