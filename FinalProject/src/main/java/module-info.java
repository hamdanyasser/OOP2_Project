module com.example.finalproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.auth0.jwt;
    requires jbcrypt;
    requires itextpdf;
    requires java.mail;

    opens com.example.finalproject to javafx.fxml;
    opens com.example.finalproject.controller to javafx.fxml;
    opens com.example.finalproject.model to javafx.base;

    exports com.example.finalproject;
    exports com.example.finalproject.controller;
}
