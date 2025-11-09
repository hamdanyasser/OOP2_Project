package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.OrderDao;
import com.example.finalproject.model.Order;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.JwtService;
import com.example.finalproject.security.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistoryController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, Double> colTotal;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, Timestamp> colDate;

    private final OrderDao orderDao = new OrderDao();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        colTotal.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("total"));
        colStatus.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        colDate.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("createdAt"));
        loadOrders();
    }

    private void loadOrders() {
        int userId = JwtService.getUserId(Session.getToken());
        List<Order> allOrders = orderDao.findAll();

        // Filter only this user's orders
        List<Order> userOrders = allOrders.stream()
                .filter(o -> o.getUserId() == userId)
                .collect(Collectors.toList());

        ObservableList<Order> orders = FXCollections.observableArrayList(userOrders);
        orderTable.setItems(orders);
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/customer_home.fxml");
    }

    @FXML
    private void onViewItems() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select an order first.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/finalproject/view/order_items_popup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Order Details");
            stage.setScene(new javafx.scene.Scene(loader.load()));

            OrderItemsPopupController controller = loader.getController();
            controller.setOrderId(selected.getId());

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }
}
