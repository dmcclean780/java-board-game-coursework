package com.example.viewmodel;

import java.util.ArrayList;

import com.example.model.AdjacencyMaps;
import com.example.model.GameModel;
import com.example.model.Player;
import com.example.model.Road;
import com.example.model.Tile;
import com.example.service.NavigationService;
import com.example.model.Settlement;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleObjectProperty;

/**
 * ViewModel for the main game screen.
 * Owns game flow and bridges model -> view.
 */
public final class GameViewModel {

    private GameModel gameModel;
    private NavigationService navigationService;

    private final ObservableList<TileViewState> tiles = FXCollections.observableArrayList();
    private TurnState turnState = TurnState.DICE_ROLL;
    private int currentPlayerIndex = 0;
    private final ObservableList<RoadViewState> roads = FXCollections.observableArrayList();
    private final ObservableList<VertexViewState> vertices = FXCollections.observableArrayList();
    private final ObservableList<PlayerViewState> players = FXCollections.observableArrayList();
    private final ObservableList<PortViewState> ports = FXCollections.observableArrayList();

    private final ObjectProperty<PlayerViewState> currentPlayer = new SimpleObjectProperty<>();

    public GameViewModel(GameModel gameModel, NavigationService navigationService) {
        this.gameModel = gameModel;
        this.navigationService = navigationService;

        // Initialize TileViewStates
        for (Tile tile : gameModel.getTiles()) {
            TileViewState tileState = new TileViewState();
            tileState.number.set(tile.getNumber());
            tileState.resource.set(tile.getTileID());
            tileState.blocked.set(tile.getIsBlocked());
            tiles.add(tileState);
        }

        Settlement[] settlements = gameModel.getSettlements();
        for (int i = 0; i < settlements.length; i++) {
            VertexViewState vertexState = new VertexViewState();
            vertexState.owner.set(settlements[i].getPlayerID());
            vertexState.type.set(settlements[i].getSettlementType());
            vertexState.visible.set(isVertexOwned(i));
            vertices.add(vertexState);
        }

        // Initialize PlayerViewStates
        ArrayList<Player> modelPlayers = gameModel.getPlayers();
        for (int i = 0; i < modelPlayers.size(); i++) {
            PlayerViewState playerState = new PlayerViewState();
            playerState.nameProperty().set(modelPlayers.get(i).getName());
            playerState.idProperty().set(modelPlayers.get(i).getId());
            playerState.canBuildSettlementProperty().set(gameModel.playerHasSettlementResources(i));
            playerState.canBuildCityProperty().set(gameModel.playerHasCityResources(i));
            players.add(playerState);
        }

        // Initialize RoadViewStates
        Road[] modelRoads = gameModel.getRoads();
        int[][] roadConnections = AdjacencyMaps.RoadConnections;
        for (int i = 0; i < roadConnections.length; i++) {
            RoadViewState roadState = new RoadViewState();
            roadState.owner.set(modelRoads[i].getPlayerID());
            roadState.visible.set(isRoadOwned(i)); // owner defaults to -1
            roads.add(roadState);
        }
    }

    public ObservableList<TileViewState> tilesProperty() {
        return tiles;
    }

    public ObservableList<VertexViewState> verticesProperty() {
        return vertices;
    }

    public ObservableList<PlayerViewState> playersProperty() {
        return players;
    }

    public PlayerViewState getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    public TurnState getTurnState() {
        return turnState;
    }

    public ObservableList<RoadViewState> roadsProperty() {
        return roads;
    }

    public ObservableList<PortViewState> portsProperty() {
        return ports;
    }

    public int[][] getTileVertices() {
        return AdjacencyMaps.TileVertices;
    }

    private void buildSettlement(int vertexIndex) {
        if (turnState != TurnState.BUILD_SETTLEMENT) {
            return;
        }

        boolean success = gameModel.buildSettlement(vertexIndex, currentPlayerIndex);
        if (success) {
            int playerID = gameModel.getPlayers().get(currentPlayerIndex).getId();
            vertices.get(vertexIndex).owner.set(playerID);
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
        }
    }

    private void buildCity(int vertexIndex) {
        if (turnState != TurnState.BUILD_CITY) {
            return;
        }

        boolean success = gameModel.buildCity(vertexIndex, currentPlayerIndex);
        if (success) {
            int playerID = gameModel.getPlayers().get(currentPlayerIndex).getId();
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
            vertices.get(vertexIndex).owner.set(playerID);
        }
    }

    private void buildRoad(int roadIndex) {
        if (turnState != TurnState.BUILD_ROAD) {
            return;
        }
        boolean success = gameModel.buildRoad(roadIndex, currentPlayerIndex);
        if (success) {
            int playerID = gameModel.getPlayers().get(currentPlayerIndex).getId();
            roads.get(roadIndex).owner.set(playerID);
        }
    }

    public void onVertexClicked(int vertexIndex) {
        switch (turnState) {
            case BUILD_SETTLEMENT -> {
                buildSettlement(vertexIndex);
                switchToBuildState();
            }
            case BUILD_CITY -> {
                buildCity(vertexIndex);
                switchToBuildState();
            }
            default -> {
                // No action
            }
        }
    }

    public void onRoadClicked(int roadIndex) {
        switch (turnState) {
            case BUILD_ROAD -> {
                buildRoad(roadIndex);
                switchToBuildState();
            }
            default -> {
                // No action
            }
        }
    }

    private boolean canCurrentPlayerBuildSettlement(int i) {

        return gameModel.settlementValid(i, currentPlayerIndex);
    }

    private boolean canCurrentPlayerBuildRoad(int i) {
        return gameModel.roadValid(i, currentPlayerIndex);
    }

    private boolean canCurrentPlayerBuildCity(int i) {
        return gameModel.cityValid(i, currentPlayerIndex);
    }

    private boolean isVertexOwned(int i) {
        return gameModel.getSettlements()[i].getPlayerID() != -1;
    }

    private boolean isRoadOwned(int i) {
        return gameModel.getRoads()[i].getPlayerID() != -1;
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        currentPlayer.set(players.get(currentPlayerIndex));
    }

    public void switchToRollDiceState() {
        turnState = TurnState.DICE_ROLL;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
    }

    public void switchToTradeState() {
        turnState = TurnState.TRADE;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
    }

    public void switchToBuildState() {
        turnState = TurnState.BUILD;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
        players.get(currentPlayerIndex).canBuildSettlementProperty()
                .set(gameModel.playerHasSettlementResources(currentPlayerIndex));
        players.get(currentPlayerIndex).canBuildCityProperty()
                .set(gameModel.playerHasCityResources(currentPlayerIndex));
        players.get(currentPlayerIndex).canBuildRoadProperty()
                .set(gameModel.playerHasRoadResources(currentPlayerIndex));
        currentPlayer.set(players.get(currentPlayerIndex));
    }

    public void switchToBuildSettlementState() {
        if (!gameModel.playerHasSettlementResources(currentPlayerIndex)) {
            return;
        }
        turnState = TurnState.BUILD_SETTLEMENT;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(canCurrentPlayerBuildSettlement(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
    }

    public void switchToBuildRoadState() {
        turnState = TurnState.BUILD_ROAD;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(canCurrentPlayerBuildRoad(i));
        }
    }

    public void switchToBuildCityState() {
        if (!gameModel.playerHasCityResources(currentPlayerIndex)) {
            return;
        }
        turnState = TurnState.BUILD_CITY;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(canCurrentPlayerBuildCity(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
    }

    public void endTurn() {
        nextPlayer();
        switchToRollDiceState();
    }

    public int[][] getRoads() {
        return AdjacencyMaps.RoadConnections;
    }

    public int[][] getPorts() {
        return AdjacencyMaps.PortVertices;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public ObjectProperty<PlayerViewState> currentPlayerProperty() {
        return currentPlayer;
    }

    // TESTING METHODS
    public void giveCityResources() {
        gameModel.giveCityResources(currentPlayerIndex);
    }

    public void giveSettlementResources() {
        gameModel.giveSettlementResources(currentPlayerIndex);
    }

    public void giveRoadResources() {
        gameModel.giveRoadResources(currentPlayerIndex);
    }
}
