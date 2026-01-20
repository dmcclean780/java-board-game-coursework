module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires transitive javafx.graphics;

    opens com.example.view to javafx.fxml;
    exports com.example.view;

    // [ Josh Added this to link ViewModel and View ]
    exports com.example.viewmodel;
    opens com.example.viewmodel to javafx.fxml;
}
