package com.example.viewmodel;

import java.util.ArrayList;
import java.util.List;

import com.example.model.Tile;
import com.example.model.Tiles;
import com.example.view.GameScreenV;

import javafx.application.Platform;
import javafx.scene.paint.Color;

/**
 * ViewModel for the main game screen.
 * Owns game flow and bridges model -> view.
 */
public final class GameScreenVM {

    private final GameScreenV view;
    private final Tiles tilesModel;
    private final Tile[] tiles;


    public GameScreenVM(GameScreenV view) {
        this.view = view;
        this.tilesModel = new Tiles();
        this.tiles = tilesModel.getTiles();
    }

    public void pushInitialStateToView() {
        // Ensure tile array exists
        if (tiles == null || view == null) return;

        // Wait until the view's tiles are initialized
        Platform.runLater(() -> {
            for (int i = 0; i < tiles.length; i++) {
                final int index = i; // required for lambda
                Tile tile = tiles[i];

                // Determine color based on resource type
                Color tileColor = resolveResourceColor(tile.getTileID());

                // Update the tile in the view
                view.setTile(index, tile.getNumber(), tileColor);

                // If tile is blocked (desert), disable it
                if (tile.getIsBlocked()) {
                    view.setTileDisabled(index, true);
                }
            }
        });
    }

    /** Safely update a tile from the model */
    public void updateTile(int index) {
        if (index < 0 || index >= tiles.length) return;
        Tile t = tiles[index];

        // Use Platform.runLater to be safe on FX thread
        javafx.application.Platform.runLater(() -> 
            view.setTile(index, t.getNumber(), resolveResourceColor(t.getTileID()))
        );
    }

    public void updateVertex(int vertexId, int playerOwner, int contains) 
    {
        if (view == null) return;

        // Ensure safe FX thread update
        Platform.runLater(() -> view.setVertex(vertexId, playerOwner, contains));
    }

    
    public void populateBoard() 
    {
        Platform.runLater(() -> {
            for (int i = 0; i < tiles.length; i++) {
                Tile tile = tiles[i];
                view.setTile(i, tile.getNumber(), resolveResourceColor(tile.getTileID()));
                if (tile.getIsBlocked()) {
                    view.setTileDisabled(i, true);
                }
            }
        });
    }

    private Color resolveResourceColor(String tileID) {
        var tileConfig = com.example.model.config.service.ConfigService.getTile(tileID);

        // DEBUG: print to verify mapping
        System.out.println("TileID: " + tileID + ", resourceID: " + tileConfig.resourceID);

        return switch (tileConfig.resourceID) {
            case "resource.wood" -> Color.FORESTGREEN;
            case "resource.brick" -> Color.DARKRED;
            case "resource.wheat" -> Color.GOLD;
            case "resource.sheep" -> Color.DARKKHAKI;
            case "resource.ore" -> Color.GRAY;
            case "" -> Color.rgb(198, 170, 71); // desert
            default -> Color.LIGHTGRAY;
        };
    }

    public int[][] getTileVertices() {
        return Tiles.getTileVertices();
    }
}

