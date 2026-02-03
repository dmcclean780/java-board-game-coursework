package com.example.viewmodel;

import java.util.ArrayList;
import java.util.Map;

import com.example.model.AdjacencyMaps;
import com.example.model.GameModel;
import com.example.model.Player;
import com.example.model.Road;
import com.example.model.Tile;
import com.example.model.config.ResourceConfig;
import com.example.service.NavigationService;
import com.example.model.Settlement;

import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
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
    private final ObservableList<RoadViewState> roads = FXCollections.observableArrayList();
    private final ObservableList<VertexViewState> vertices = FXCollections.observableArrayList();
    private final ObservableList<PlayerViewState> players = FXCollections.observableArrayList(); // All players except
                                                                                                 // current
    private final ObjectProperty<PlayerViewState> currentPlayer = new SimpleObjectProperty<>(); // Current player
    private final ObservableList<PortViewState> ports = FXCollections.observableArrayList();

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

        Player firstPlayer = modelPlayers.get(0);
        PlayerViewState firstPlayerState = setUpPlayerViewState(firstPlayer);
        currentPlayer.set(firstPlayerState);

        for (int i = 1; i < modelPlayers.size(); i++) {
            PlayerViewState playerState = setUpPlayerViewState(modelPlayers.get(i));
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

    private PlayerViewState setUpPlayerViewState(Player player) {
        PlayerViewState playerState = new PlayerViewState();
        playerState.idProperty().set(player.getId());
        playerState.nameProperty().set(player.getName());
        playerState.canBuildSettlementProperty().set(gameModel.playerHasSettlementResources(player.getId()));
        playerState.canBuildCityProperty().set(gameModel.playerHasCityResources(player.getId()));
        playerState.canBuildRoadProperty().set(gameModel.playerHasRoadResources(player.getId()));
        playerState.scoreProperty().set(0);
        playerState.longestRoadProperty().set(false);
        playerState.cleanestEnvironmentProperty().set(false);
        playerState.colorProperty().set(getPlayerColor(player.getId() - 1));

        initPlayerResources(playerState);
        return playerState;
    }

    private PlayerViewState updatePlayerViewState(PlayerViewState playerState) {
        Player player = gameModel.getPlayer(playerState.idProperty().get());
        playerState.canBuildSettlementProperty().set(gameModel.playerHasSettlementResources(player.getId()));
        playerState.canBuildCityProperty().set(gameModel.playerHasCityResources(player.getId()));
        playerState.canBuildRoadProperty().set(gameModel.playerHasRoadResources(player.getId()));
        // playerState.scoreProperty().set(player.getScore());
        // playerState.longestRoadProperty().set(gameModel.playerHasLongestRoad(player.getId()));
        // playerState.cleanestEnvironmentProperty().set(gameModel.playerHasCleanestEnvironment(player.getId()));
        updateResourceCounts(playerState);
        return playerState;
    }

    private void initPlayerResources(PlayerViewState playerState) {
        Player player = gameModel.getPlayer(playerState.idProperty().get());

        for (ResourceConfig type : player.getResourcesMap().keySet()) {
            ResourceViewState rvs = new ResourceViewState();
            rvs.configProperty().set(type);
            rvs.countProperty().set(0);
            playerState.getResources().add(rvs);
        }
    }

    private void updateResourceCounts(PlayerViewState playerState) {
        Player player = gameModel.getPlayer(playerState.idProperty().get());
        Map<ResourceConfig, Integer> resources = player.getResourcesMap();

        for (ResourceViewState rvs : playerState.getResources()) {
            Integer newValue = resources.get(rvs.configProperty().get());
            if (newValue != null) {
                rvs.countProperty().set(newValue);
            }
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
        return currentPlayer.get();
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

        boolean success = gameModel.buildSettlement(vertexIndex, getCurrentPlayer().idProperty().get());
        if (success) {
            int playerID = getCurrentPlayer().idProperty().get();
            vertices.get(vertexIndex).owner.set(playerID);
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
        }
    }

    private void buildCity(int vertexIndex) {
        if (turnState != TurnState.BUILD_CITY) {
            return;
        }

        boolean success = gameModel.buildCity(vertexIndex, getCurrentPlayer().idProperty().get());
        if (success) {
            int playerID = getCurrentPlayer().idProperty().get();
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
            vertices.get(vertexIndex).owner.set(playerID);
        }
    }

    private void buildRoad(int roadIndex) {
        if (turnState != TurnState.BUILD_ROAD) {
            return;
        }
        boolean success = gameModel.buildRoad(roadIndex, currentPlayer.get().idProperty().get());
        if (success) {
            int playerID = currentPlayer.get().idProperty().get();
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

        return gameModel.settlementValid(i, getCurrentPlayer().idProperty().get());
    }

    private boolean canCurrentPlayerBuildRoad(int i) {
        return gameModel.roadValid(i, getCurrentPlayer().idProperty().get());
    }

    private boolean canCurrentPlayerBuildCity(int i) {
        return gameModel.cityValid(i, getCurrentPlayer().idProperty().get());
    }

    private boolean isVertexOwned(int i) {
        return gameModel.getSettlements()[i].getPlayerID() != -1;
    }

    private boolean isRoadOwned(int i) {
        return gameModel.getRoads()[i].getPlayerID() != -1;
    }

    private int getIndexOfPlayerWithID(int playerID) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).idProperty().get() == playerID) {
                return i;
            }
        }
        return -1; // Not found
    }

    public void nextPlayer() {
        int nextPlayerID = gameModel.nextPlayer(getCurrentPlayer().idProperty().get());
        players.add(getCurrentPlayer());
        int nextPlayerIndex = getIndexOfPlayerWithID(nextPlayerID);
        currentPlayer.set(players.remove(nextPlayerIndex));
    }

    public void switchToRollDiceState() {
        turnState = TurnState.DICE_ROLL;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
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
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
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
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
        }
    }

    public void switchToBuildSettlementState() {
        if (!gameModel.playerHasSettlementResources(getCurrentPlayer().idProperty().get())) {
            return;
        }
        turnState = TurnState.BUILD_SETTLEMENT;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(canCurrentPlayerBuildSettlement(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
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
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
        }
    }

    public void switchToBuildCityState() {
        if (!gameModel.playerHasCityResources(getCurrentPlayer().idProperty().get())) {
            return;
        }
        turnState = TurnState.BUILD_CITY;
        for (int i = 0; i < vertices.size(); i++) {
            vertices.get(i).visible.set(canCurrentPlayerBuildCity(i));
        }
        for (int i = 0; i < roads.size(); i++) {
            roads.get(i).visible.set(isRoadOwned(i));
        }
        updatePlayerViewState(getCurrentPlayer());
        for (int i = 0; i < players.size(); i++) {
            updatePlayerViewState(players.get(i));
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

    public ObjectProperty<PlayerViewState> currentPlayerProperty() {
        return currentPlayer;
    }

    // TESTING METHODS
    public void giveCityResources() {
        gameModel.giveCityResources(getCurrentPlayer().idProperty().get());
    }

    public void giveSettlementResources() {
        gameModel.giveSettlementResources(getCurrentPlayer().idProperty().get());
    }

    public void giveRoadResources() {
        gameModel.giveRoadResources(getCurrentPlayer().idProperty().get());
    }

    private static final Color[] PLAYER_COLOURS = {
            Color.web("#e43b29"), // player 1 red
            Color.web("#4fa6eb"), // player 2 blue
            Color.web("#f0ad00"), // player 3 yellow
            Color.web("#517d19") // player 4 green
    };

    private static final Color UNOWNED_COLOR = Color.GRAY;

    public Color getPlayerColor(int owner) {
        return (owner >= 0 && owner < PLAYER_COLOURS.length)
                ? PLAYER_COLOURS[owner]
                : UNOWNED_COLOR;
    }
}
