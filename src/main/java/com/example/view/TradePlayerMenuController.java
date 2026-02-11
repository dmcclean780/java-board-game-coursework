package com.example.view;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;

import com.example.model.config.LangManager;
import com.example.model.config.PortConfig;
import com.example.model.config.ResourceConfig;
import com.example.viewmodel.GameViewModel;
import com.example.viewmodel.viewstates.BankViewState;
import com.example.viewmodel.viewstates.GameUIState;
import com.example.viewmodel.viewstates.PlayerViewState;
import com.example.viewmodel.viewstates.ResourceViewState;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleBooleanProperty;

public class TradePlayerMenuController {
    private GameViewModel viewModel;

    @FXML
    private Label playerTradeTitleLabel;
    @FXML
    private Button confirmTradeButton;

    @FXML
    private HBox giveResourceBox;
    @FXML
    private HBox receiveResourceBox;
    private ToggleGroup playerToggleGroup = new ToggleGroup();

    private HashMap<ResourceConfig, Integer> selectedGiveResources = new HashMap<>();
    private HashMap<ResourceConfig, Integer> selectedReceiveResources = new HashMap<>();
    private BooleanProperty tradeProposed = new SimpleBooleanProperty(false);

    public void bind(GameViewModel viewModel) {
        this.viewModel = viewModel;
        viewModel.currentPlayerProperty().addListener((obs, oldPlayer, newPlayer) -> {
            updateResourceBoxes(viewModel);
        });
        updateResourceBoxes(viewModel);

        GameUIState.popupVisible.addListener((obs, oldValue, newValue) -> {
            tradeProposed.set(false);
            selectedGiveResources.clear();
            selectedReceiveResources.clear();
        });
    }

    public void initialize() {
        playerTradeTitleLabel.setText(LangManager.get("playerTradeTitleLabel"));
        confirmTradeButton.setText(LangManager.get("confirmTradeButton"));
    }

    private void updateResourceBoxes(GameViewModel viewModel) {
        giveResourceBox.getChildren().clear();
        receiveResourceBox.getChildren().clear();

        ObjectProperty<PlayerViewState> currentPlayer = viewModel.currentPlayerProperty();
        currentPlayer.get().getResources().forEach(resourceViewState -> {
            Spinner<Integer> resourceSelector = createResourceGiveSelector(resourceViewState);
            giveResourceBox.getChildren().add(resourceSelector);
        });

        currentPlayer.get().getResources().forEach(resourceViewState -> {
            Spinner<Integer> resourceSelector = createResourceReceiveSelector(resourceViewState,
                    viewModel.playersProperty());
            receiveResourceBox.getChildren().add(resourceSelector);
        });
    }

    private Spinner<Integer> createResourceGiveSelector(ResourceViewState resourceViewState) {
        Spinner<Integer> spinner = new Spinner<>(0, resourceViewState.countProperty().get(), 0);
        spinner.setEditable(true);
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue > resourceViewState.countProperty().get()) {
                spinner.getValueFactory().setValue(resourceViewState.countProperty().get());
            } else if (newValue < 0) {
                spinner.getValueFactory().setValue(0);
            }
        });
        spinner.disableProperty().bind(tradeProposed);
        spinner.setUserData(resourceViewState.configProperty().get());
        return spinner;
    }

    private Spinner<Integer> createResourceReceiveSelector(ResourceViewState resourceViewState,
            ObservableList<PlayerViewState> players) {

        int max = players.stream()
                .mapToInt(player -> player.getResources().stream()
                        .filter(r -> r.configProperty().get().equals(
                                resourceViewState.configProperty().get()))
                        .mapToInt(r -> r.countProperty().get())
                        .sum())
                .max()
                .orElse(0);
        Spinner<Integer> spinner = new Spinner<>(0, max, 0);
        spinner.setEditable(true);
        spinner.valueProperty().addListener((obs, oldValue, newValue) -> {

            HashMap<ResourceConfig, Integer> selected = getSelectedResources(receiveResourceBox);

            if (!canAnyPlayerProvide(selected, players)) {
                // revert change
                spinner.getValueFactory().setValue(oldValue);
            }
        });
        spinner.disableProperty().bind(tradeProposed);
        spinner.setUserData(resourceViewState.configProperty().get());
        return spinner;
    }

    private HashMap<ResourceConfig, Integer> getSelectedResources(HBox selectionBox) {

        HashMap<ResourceConfig, Integer> selection = new HashMap<>();

        selectionBox.getChildren().stream()
                .filter(n -> n instanceof Spinner<?>)
                .map(n -> (Spinner<Integer>) n)
                .forEach(spinner -> {
                    ResourceConfig resource = (ResourceConfig) spinner.getUserData();
                    selection.put(resource, spinner.getValue());
                });

        return selection;
    }

    private boolean canAnyPlayerProvide(
            HashMap<ResourceConfig, Integer> required,
            ObservableList<PlayerViewState> players) {

        return players.stream().anyMatch(player -> {

            return required.entrySet().stream().allMatch(entry -> {

                ResourceConfig resource = entry.getKey();
                int requiredAmount = entry.getValue();

                int playerAmount = player.getResources().stream()
                        .filter(r -> r.configProperty().get().equals(resource))
                        .mapToInt(r -> r.countProperty().get())
                        .sum();

                return playerAmount >= requiredAmount;
            });

        });
    }

    @FXML
    private void handleProposeTrade() {
        System.out.println("Proposing player trade...");

        selectedGiveResources = getSelectedResources(giveResourceBox);
        selectedReceiveResources = getSelectedResources(receiveResourceBox);
    }

    @FXML
    private void handleConfirmTrade() {
        System.out.println("Confirming bank trade...");
        int playerID = (int) playerToggleGroup.getSelectedToggle().getUserData();
    }
}
