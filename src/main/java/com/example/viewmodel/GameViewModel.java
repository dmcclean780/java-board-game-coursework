package com.example.viewmodel;

import java.util.ArrayList;

import com.example.model.GameModel;
import com.example.model.Player;
import com.example.model.Tile;
import com.example.model.Tiles;
import com.example.service.NavigationService;

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

        for (int i = 0; i < gameModel.getNumberOfVertices(); i++) {
            vertices.add(new VertexViewState());
        }

        // Initialize PlayerViewStates
        ArrayList<Player> modelPlayers = gameModel.getPlayers();
        for (int i = 0; i < modelPlayers.size(); i++) {
            PlayerViewState playerState = new PlayerViewState();
            playerState.nameProperty().set(modelPlayers.get(i).getName());
            playerState.idProperty().set(i);
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

    public int[][] getTileVertices() {
        return Tiles.getTileVertices();
    }
}
