module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive com.fasterxml.jackson.databind;
    requires transitive javafx.graphics;

    opens com.example.view to javafx.fxml;
    exports com.example.view;

    exports com.example.model.config; // gets rid of strange warnings; added by ethan
    exports com.example.model.trading; 

    // [ Josh Added this to link ViewModel and View ]
    exports com.example.viewmodel;
    exports com.example.model;
    exports com.example.service;
    opens com.example.viewmodel to javafx.fxml;
}
