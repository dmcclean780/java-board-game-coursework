package com.example.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.view.ViewModelAware;

public class NavigationService {

    private final Stage primaryStage;
    private Scene mainScene;
    private boolean initialized = false;

    public NavigationService(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @SuppressWarnings("unchecked")
    public <T> void navigateTo(String fxmlName, T viewModel) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/fxml/" + fxmlName + ".fxml")
            );
            Parent root = loader.load();

            // Set ViewModel if controller supports it
            Object controller = loader.getController();
            if (controller instanceof ViewModelAware<?> vmAware) {
                ((ViewModelAware<T>) vmAware).setViewModel(viewModel);
            }

            // Initialize scene once
            if (!initialized) {
                mainScene = new Scene(root);
                loadFontAndStyles(mainScene);
                primaryStage.setScene(mainScene);
                initialized = true;
            } else {
                mainScene.setRoot(root);
            }

            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlName, e);
        }
    }

    private void loadFontAndStyles(Scene scene) {
        // Load font once

        // Add CSS once
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    }
}
