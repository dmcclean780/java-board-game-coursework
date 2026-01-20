package com.example.viewmodel;

import java.util.ArrayList;

import com.example.model.GameModel;
import com.example.model.Player;
import com.example.model.Tile;
import com.example.model.Tiles;
import com.example.service.NavigationService;
import com.example.model.Settlement;
import com.example.model.Settlements;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * ViewModel for the main game screen.
 * Owns game flow and bridges model -> view.
 */
public final class GameViewModel {

    private GameModel gameModel;
    private NavigationService navigationService;

    private final ObservableList<TileViewState> tiles = FXCollections.observableArrayList();
    private final ObservableList<VertexViewState> vertices = FXCollections.observableArrayList();
    private final ObservableList<PlayerViewState> players = FXCollections.observableArrayList();

    private TurnState turnState = TurnState.DICE_ROLL;
    private int currentPlayerIndex = 0;

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
            players.add(playerState);
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

    public int[][] getTileVertices() {
        return Tiles.getTileVertices();
    }

    private void buildSettlement(int vertexIndex) {
        if (turnState != TurnState.BUILD_SETTLEMENT) {
            return;
        }

        int playerID = gameModel.getPlayers().get(currentPlayerIndex).getId();
        boolean success = gameModel.buildSettlement(vertexIndex, playerID);
        if (success) {
            vertices.get(vertexIndex).owner.set(playerID);
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
        }
    }

    private void buildCity(int vertexIndex) {
        if (turnState != TurnState.BUILD_CITY) {
            return;
        }

        int playerID = gameModel.getPlayers().get(currentPlayerIndex).getId();
        boolean success = gameModel.buildCity(vertexIndex, playerID);
        if (success) {
            vertices.get(vertexIndex).type.set(gameModel.getSettlmentType(vertexIndex));
            vertices.get(vertexIndex).owner.set(playerID);
            System.out.println("Settlement built at vertex " + vertexIndex + " by player " + playerID);
        }
    }

    public void onVertexClicked(int vertexIndex) {
        System.out.println("Vertex clicked: " + vertexIndex);
        switch (turnState) {
            case BUILD_SETTLEMENT -> {
                buildSettlement(vertexIndex);
                switchToBuildState();
            }
            case BUILD_ROAD -> {
                // buildRoad(vertexIndex);
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

    private boolean canCurrentPlayerBuildSettlement(int i) {
        
        return gameModel.settlementValid(i, currentPlayerIndex);
    }

    private boolean canCurrentPlayerBuildRoad(int i) {
        return false; // TODO: Implement road building logic
    }

    private boolean canCurrentPlayerBuildCity(int i) {
        return gameModel.cityValid(i, currentPlayerIndex);
    }

    private boolean isVertexOwned(int i) {
        return gameModel.getSettlements()[i].getPlayerID() != -1;
    }

    public void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    public void switchToRollDiceState() {
        turnState = TurnState.DICE_ROLL;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
    }

    public void switchToTradeState() {
        turnState = TurnState.TRADE;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
    }

    public void switchToBuildState() {
        turnState = TurnState.BUILD;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
    }

    public void switchToBuildSettlementState() {
        turnState = TurnState.BUILD_SETTLEMENT;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(canCurrentPlayerBuildSettlement(i));
        }
    }

    public void switchToBuildRoadState() {
        turnState = TurnState.BUILD_ROAD;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(isVertexOwned(i));
        }
    }

    public void switchToBuildCityState() {
        turnState = TurnState.BUILD_CITY;
        for( int i = 0; i < vertices.size(); i++ ) {
            vertices.get(i).visible.set(canCurrentPlayerBuildCity(i));
        }
    }

    public void endTurn() {
        nextPlayer();
        switchToRollDiceState();
    }


}
