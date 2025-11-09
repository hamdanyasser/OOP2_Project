package com.example.finalproject.controller;

import com.example.finalproject.HelloApplication;
import com.example.finalproject.model.Order;
import com.example.finalproject.security.AuthGuard;
import com.example.finalproject.security.Session;
import com.example.finalproject.service.InvoiceService;
import com.example.finalproject.service.OrderService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminOrdersController {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order, Integer> colId;
    @FXML private TableColumn<Order, Integer> colUser;
    @FXML private TableColumn<Order, Double> colTotal;
    @FXML private TableColumn<Order, String> colStatus;
    @FXML private TableColumn<Order, String> colDate;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    private final OrderService service = new OrderService();

    @FXML
    public void initialize() {
        AuthGuard.requireLogin();
        colId.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getId()).asObject());
        colUser.setCellValueFactory(c -> new javafx.beans.property.SimpleIntegerProperty(c.getValue().getUserId()).asObject());
        colTotal.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getTotal()).asObject());
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStatus()));
        colDate.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt().toString()));

        statusFilter.getItems().addAll("ALL", "PENDING", "DELIVERED");
        statusFilter.setValue("ALL");
        loadData();
    }

    private void loadData() {
        orderTable.setItems(FXCollections.observableArrayList(service.getAllOrders()));
    }

    @FXML
    private void onDeliver() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.markDelivered(selected.getId());
            loadData();
        } else showAlert("Select an order to mark as delivered.");
    }

    @FXML
    private void onDelete() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            service.deleteOrder(selected.getId());
            loadData();
        } else showAlert("Select an order to delete.");
    }



    @FXML
    private void onApplyFilter() {
        String keyword = searchField.getText().trim();
        String status = statusFilter.getValue();

        orderTable.setItems(FXCollections.observableArrayList(service.filterOrders(keyword, status)));
    }

    @FXML
    private void onResetFilter() {
        searchField.clear();
        statusFilter.setValue("ALL");
        loadData();
    }

    @FXML
    private void onViewReports() {
        HelloApplication.setRoot("view/admin_reports.fxml");
    }

    @FXML
    private void onExportCSV() {
        try {
            java.io.File file = new java.io.File("orders_export.csv");
            try (java.io.FileWriter writer = new java.io.FileWriter(file)) {
                writer.write("OrderID,UserID,Total,Status,CreatedAt\n");
                for (var order : service.getAllOrders()) {
                    writer.write(order.getId() + "," +
                            order.getUserId() + "," +
                            order.getTotal() + "," +
                            order.getStatus() + "," +
                            order.getCreatedAt() + "\n");
                }
            }
            showAlert("✅ Orders exported to: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Error exporting CSV: " + e.getMessage());
        }
    }

    @FXML
    private void onExportPDF() {
        try {
            String pdfPath = "orders_export.pdf";

            com.itextpdf.text.Document doc = new com.itextpdf.text.Document();
            com.itextpdf.text.pdf.PdfWriter.getInstance(doc,
                    new java.io.FileOutputStream(pdfPath));
            doc.open();

            doc.add(new com.itextpdf.text.Paragraph("Orders Report"));
            doc.add(new com.itextpdf.text.Paragraph(" "));

            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(5);
            table.addCell("Order ID");
            table.addCell("User ID");
            table.addCell("Total");
            table.addCell("Status");
            table.addCell("Created At");

            for (var o : service.getAllOrders()) {
                table.addCell(String.valueOf(o.getId()));
                table.addCell(String.valueOf(o.getUserId()));
                table.addCell(String.format("%.2f", o.getTotal()));
                table.addCell(o.getStatus());
                table.addCell(o.getCreatedAt().toString());
            }

            doc.add(table);
            doc.close();

            showAlert("✅ PDF generated at: " + pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Error generating PDF: " + e.getMessage());
        }
    }

    @FXML
    private void goProducts() {
        try {
            com.example.finalproject.HelloApplication.setRoot("view/admin_products.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onDownloadInvoice() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an order first.");
            return;
        }

        try {
            java.nio.file.Path pdf = new InvoiceService().generateInvoice(selected.getId());
            showAlert("✅ Invoice saved to: " + pdf.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("❌ Failed to generate invoice: " + e.getMessage());
        }
    }


    @FXML
    private void logout() {
        Session.clear();
        HelloApplication.setRoot("view/login.fxml");
    }

    @FXML
    private void onViewItems() {
        Order selected = orderTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Please select an order first.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/finalproject/view/order_items_popup.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Order #" + selected.getId() + " Details");
            stage.setScene(new javafx.scene.Scene(loader.load()));

            // Pass order ID to popup controller
            OrderItemsPopupController controller = loader.getController();
            controller.setOrderId(selected.getId());

            // Optional: make it modal (blocks background window)
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading order details: " + e.getMessage());
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }


}
