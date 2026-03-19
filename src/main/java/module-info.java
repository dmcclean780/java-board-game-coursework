module com.example {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive javafx.graphics;

    opens com.example.view to javafx.fxml;
    exports com.example.view;

    exports com.example.model.config;
    exports com.example.model.trading; 

    exports com.example.viewmodel;
    exports com.example.model;
    exports com.example.service;
    opens com.example.viewmodel to javafx.fxml;
}
