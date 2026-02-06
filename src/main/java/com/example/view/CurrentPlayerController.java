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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
    @FXML
    private HBox diceDisplayBox;

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

        // Dice display
        updateDiceDisplay();

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
        updateDiceDisplay();
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

    private void updateDiceDisplay() {
        int die1 = viewModel.getGameModel().getDice().getDie1();
        int die2 = viewModel.getGameModel().getDice().getDie2();
        displayDiceImages(die1, die2);
    }

    private void displayDiceImages(int die1, int die2) {
        if (diceDisplayBox == null) return;

        diceDisplayBox.getChildren().clear();

        // Die 1 image, Red
        ImageView die1View = createDiceImageView(die1, "RDie");
        if (die1View != null) {
            diceDisplayBox.getChildren().add(die1View);
        }

        // Die 2 image, Yellow
        ImageView die2View = createDiceImageView(die2, "YDie");
        if (die2View != null) {
            diceDisplayBox.getChildren().add(die2View);
        }
    }

    private ImageView createDiceImageView(int dieValue, String prefix) {
        if (dieValue < 1 || dieValue > 6) return null;

        String imagePath = "/images/" + prefix + dieValue + ".png";
        try {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(40);
            imageView.setFitHeight(40);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            System.err.println("Failed. to load dice image: " + imagePath);
            return null;
        }
    }
}
