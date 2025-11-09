package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.dao.ReportDao;
import com.example.finalproject.model.TopProduct;
import com.example.finalproject.security.AuthGuard;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import com.example.finalproject.security.Session;

import java.io.FileWriter;
import java.util.List;

public class AdminReportsController {

    @FXML private Label totalRevenueLabel;
    @FXML private BarChart<String, Number> salesChart;
    @FXML private TableView<TopProduct> topProductsTable;
    @FXML private TableColumn<TopProduct, String> colProduct;
    @FXML private TableColumn<TopProduct, Integer> colQty;
    @FXML private TableColumn<TopProduct, Double> colRevenue;

    private final ReportDao dao = new ReportDao();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        colProduct.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getProductName()));
        colQty.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getQuantitySold()).asObject());
        colRevenue.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getRevenue()).asObject());
        loadData();
    }

    private void loadData() {
        // 🧾 Total revenue
        double total = dao.getTotalRevenue();
        totalRevenueLabel.setText("💰 Total Revenue (Delivered Orders): $" + String.format("%.2f", total));

        // 📈 Daily sales chart
        salesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        dao.getDailySales().forEach(s -> series.getData().add(new XYChart.Data<>(s.getDate(), s.getRevenue())));
        series.setName("Daily Revenue");
        salesChart.getData().add(series);

        // 🥇 Top-selling products
        topProductsTable.setItems(FXCollections.observableArrayList(dao.getTopSellingProducts()));
    }

    @FXML
    private void onRefresh() {
        loadData();
    }

    @FXML
    private void onExportCSV() {
        try (FileWriter writer = new FileWriter("sales_report.csv")) {
            writer.write("Product,Quantity Sold,Revenue\n");
            for (TopProduct tp : dao.getTopSellingProducts()) {
                writer.write(String.format("%s,%d,%.2f\n", tp.getProductName(), tp.getQuantitySold(), tp.getRevenue()));
            }
            showAlert("✅ CSV Exported: sales_report.csv");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Error exporting CSV: " + e.getMessage());
        }
    }

    @FXML
    private void onBack() {
        HelloApplication.setRoot("view/admin_orders.fxml");
    }

    @FXML
    private void onLogout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
