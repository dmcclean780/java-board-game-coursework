package com.example.view;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import com.example.model.config.DevCardConfig;
import com.example.model.config.LangManager;
import com.example.model.config.PlayerInfrastructureConfig;
import com.example.model.config.PortConfig;
import com.example.model.config.ResourceConfig;
import com.example.model.config.TileConfig;
import com.example.model.config.service.ConfigService;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

public class PrimaryController {

    @FXML
    private Label gameRulesDynamicLabel;
    @FXML
    private Label resourceDynamicLabel;
    @FXML
    private Label portDynamicLabel;
    @FXML
    private Label infrastructureDynamicLabel;
    @FXML
    private Label tilesDynamicLabel;
    @FXML
    private Label devCardDynamicLabel;

    @FXML
    private ComboBox<String> languageDropdown;

    @FXML
    private void initialize() {
        HashMap<String, String> availableLanguages = LangManager.getAvailableLanguages();
        languageDropdown.getItems().addAll(availableLanguages.values());
        String currentLang = LangManager.getCurrentLanguage();
        if (currentLang != null) {
            languageDropdown.setValue(availableLanguages.get(currentLang));
        }

        updateLabels();
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void onConfirmLanguage() {
        String selected = languageDropdown.getValue();
        if (selected != null) {
            HashMap<String, String> availableLanguages = LangManager.getAvailableLanguages();
            for (String code : availableLanguages.keySet()) {
                if (availableLanguages.get(code).equals(selected)) {
                    LangManager.setLanguage(code);
                    break;
                }
            }
        }
        updateLabels();
    }

    private void updateLabels() {
        int victoryPoints = ConfigService.getVictoryPointsToWin();
        int players = ConfigService.getGameRules().players;
        int robberCardLimit = ConfigService.getRobberCardLimit();
        HashMap<Integer, Integer> numberTokens = ConfigService.getGameRules().numberTokens;
        gameRulesDynamicLabel.setText("Victory Points to Win: " + victoryPoints + "\n Players: " + players + "\n Robber Card Limit: " + robberCardLimit + "\n Number Tokens: " + numberTokens.toString());

        Collection<ResourceConfig> resources = ConfigService.getAllResources();
        StringBuilder resourceNames = new StringBuilder("Resources:\n");
        for (ResourceConfig resource : resources) {
            resourceNames.append("Resource ID: ").append(resource.id).append(", Name: ").append(ConfigService.getDisplayName(resource.id)).append(", Quantity: ").append(resource.maxQuantity).append("\n");
        }
        resourceDynamicLabel.setText(resourceNames.toString());

        Collection<TileConfig> tiles = ConfigService.getAllTiles();
        StringBuilder tileNames = new StringBuilder("Tiles:\n");
        for (TileConfig tile : tiles) {
            tileNames.append("Tile ID: ").append(tile.id).append(", Name: ").append(ConfigService.getDisplayName(tile.id)).append(", Quantity: ").append(tile.maxQuantity).append(", Provides Resource: ").append(ConfigService.getDisplayName(tile.resourceID)).append("\n");
        }
        tilesDynamicLabel.setText(tileNames.toString());

        Collection<PortConfig> ports = ConfigService.getAllPorts();
        StringBuilder portNames = new StringBuilder("Ports:\n");
        for (PortConfig port : ports) {
            portNames.append("Port ID: ").append(port.id).append(", Name: ").append(ConfigService.getDisplayName(port.id)).append(", Trade Ratio: ").append(port.giveQuantity).append(":").append(port.receiveQuantity).append(", Resource: ").append(ConfigService.getDisplayName(port.resourceID)).append("\n");
        }
        portDynamicLabel.setText(portNames.toString());

        Collection<PlayerInfrastructureConfig> infrastructures = ConfigService.getAllInfrastructure();
        StringBuilder infrastructureNames = new StringBuilder("Player Infrastructures:\n");
        for (PlayerInfrastructureConfig infrastructure : infrastructures) {
            infrastructureNames.append("Infrastructure ID: ").append(infrastructure.id).append(", Name: ").append(ConfigService.getDisplayName(infrastructure.id)).append(", Quantity: ").append(infrastructure.maxQuantity).append(", Cost: ").append(infrastructure.constructionCosts.toString()).append("\n");
        }
        infrastructureDynamicLabel.setText(infrastructureNames.toString());

        Collection<DevCardConfig> devCards = ConfigService.getAllDevCards();
        StringBuilder devCardInfo = new StringBuilder("Development Card Info:\n");
        for (DevCardConfig devCard : devCards) {
            devCardInfo.append("Dev Card ID: ").append(devCard.id).append(", Name: ").append(ConfigService.getDisplayName(devCard.id)).append(", Cost: ").append(devCard.actionType).append(", Description: ").append(ConfigService.getDevCardDescription(devCard.id)).append(", Quantity: ").append(devCard.count).append("\n");
        }
        devCardDynamicLabel.setText(devCardInfo.toString());
    }

    @FXML
    private void switchToTitle() throws IOException {
        App.setRoot("titleScreen");
    }

    @FXML
    private void switchToTest() throws IOException {
        App.setRoot("test");
    }
}
