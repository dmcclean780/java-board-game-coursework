package com.example.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import com.example.model.GameModel;
import com.example.model.config.ConfigManager;
import com.example.service.NavigationService;
import com.example.viewmodel.TitleViewModel;
/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        GameModel model = new GameModel();

        NavigationService navigationService = new NavigationService(stage);

        // Start with the first screen, passing the model
        TitleViewModel titleVM = new TitleViewModel(model, navigationService);
        navigationService.navigateTo("titleScreen", titleVM);

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        ConfigManager.loadAll();

        launch();
    }

}