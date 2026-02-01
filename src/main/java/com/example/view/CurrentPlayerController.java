package com.example.view;

import java.io.IOException;


import com.example.viewmodel.PlayerViewState;
import com.example.viewmodel.ResourceViewState;
import com.example.viewmodel.GameViewModel;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class CurrentPlayerController {

    @FXML
    private Rectangle bottomBackground;
    @FXML
    private Label currentPlayerDisplay;
    @FXML
    private VBox resourcesBox;
    @FXML
    private Node buildSettlementButton;
    @FXML
    private Node buildCityButton;
    @FXML
    private Node buildRoadButton;

    private GameViewModel viewModel;

    public void bindCurrentPlayer(GameViewModel viewModel) {
        this.viewModel = viewModel;

        ObjectProperty<PlayerViewState> currentPlayer = viewModel.currentPlayerProperty();

        // Name
        currentPlayerDisplay.textProperty().bind(
                Bindings.selectString(currentPlayer, "name"));

        // Background color
        bottomBackground.fillProperty().bind(
                Bindings.select(currentPlayer, "color"));

        // Resources
        // 🔹 RESOURCES 🔹
        currentPlayer.addListener((obs, oldPlayer, newPlayer) -> {
            populateResources(newPlayer);
        });

        // initialize
        populateResources(currentPlayer.get());

        // Buttons
        buildSettlementButton.disableProperty().bind(
                Bindings.selectBoolean(currentPlayer, "canBuildSettlement").not());
        buildCityButton.disableProperty().bind(
                Bindings.selectBoolean(currentPlayer, "canBuildCity").not());
        buildRoadButton.disableProperty().bind(
                Bindings.selectBoolean(currentPlayer, "canBuildRoad").not());
    }

    private void populateResources(PlayerViewState player) {
        resourcesBox.getChildren().clear();

        if (player == null)
            return;

        for (ResourceViewState resource : player.getResources()) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/fxml/resourceBox.fxml"));
                Node node = loader.load();

                ResourceBoxController ctrl = loader.getController();
                ctrl.bind(resource);

                resourcesBox.getChildren().add(node);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchToBUILDSETTLEMENTPHASE() {
        viewModel.switchToBuildSettlementState();
        System.out.println(viewModel.getTurnState());
    }

    public void switchToBUILDROADPHASE() {
        viewModel.switchToBuildRoadState();
        System.out.println(viewModel.getTurnState());
    }

    public void switchToBUILDCITYPHASE() {
        viewModel.switchToBuildCityState();
        System.out.println(viewModel.getTurnState());
    }

    public void switchToROLLDICEPHASE() {
        viewModel.switchToRollDiceState();
        System.out.println(viewModel.getTurnState());
    }

    public void switchToBUILDINGPHASE() {
        viewModel.switchToBuildState();
        System.out.println(viewModel.getTurnState());
    }

    public void nextPlayer() {
        viewModel.nextPlayer();
        System.out.println("Next player: " + viewModel.getCurrentPlayer().nameProperty().get());
    }
}
