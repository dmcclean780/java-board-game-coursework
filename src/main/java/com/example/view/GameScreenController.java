package com.example.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.model.Ports;
import com.example.model.Roads;
import com.example.viewmodel.GameViewModel;
import com.example.viewmodel.TileViewState;
import com.example.viewmodel.VertexViewState;
import com.example.viewmodel.RoadViewState;
import com.example.viewmodel.PlayerViewState;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.Group;

public class GameScreenController implements ViewModelAware<GameViewModel> {
    private GameViewModel viewModel;

    @FXML
    private Font oswaldFont;
    @FXML
    private Pane vertexPane;
    @FXML
    private Pane roadsPane;
    @FXML
    private Pane portsPane;
    @FXML
    private Pane borderPane;
    @FXML
    private Polygon mainPentagon;
    @FXML
    private Pane rootPane, catanBoardPane;
    @FXML
    private Rectangle bottomBackground;
    
    @FXML private StackPane currentPlayerPane;
    @FXML private StackPane player1Pane;
    @FXML private StackPane player2Pane;
    @FXML private StackPane player3Pane;
    @FXML private Polygon currentPlayerBox;
     
    @FXML private Label currentPlayerText;

    private static final Color[] PLAYER_COLORS = {
        Color.web("#e43b29"),        // player 1 red
        Color.web("#4fa6eb"),        // player 2 blue
        Color.web("#f0ad00"),        // player 3 yellow
        Color.web("#517d19")         // player 4 green
    };

    // Static holder for names before screen loads
    private Shape[] vertexNodes = new Shape[54]; // can hold Circle or Rectangle
    private int[] vertexToPort = new int[54]; // -1 = not a port
    private Shape[] portDecorations = new Shape[54];
    private Shape[] roadNodes = new Shape[72];

    @Override
    public void setViewModel(GameViewModel viewModel) 
    {
        this.viewModel = viewModel;

        bindViewModel();
    }

    private void bindViewModel()
    {
        // --- Tiles ---
        for (int i = 0; i < tileGroup.length; i++) {
            bindTile(i, viewModel.tilesProperty().get(i));
        }

        // --- Roads ---
        for (int i = 0; i < roadNodes.length; i++) {
            bindRoad(i, viewModel.roadsProperty().get(i));
        }

        // --- Ports FIRST ---
        mapPortsToVertices(viewModel.getPorts());

        // --- Vertex positions FIRST ---
        setupAllVertices(viewModel.getTileVertices());

        // --- THEN bind vertices (calls setVertex with ports available) ---
        for (int i = 0; i < vertexNodes.length; i++) {
            bindVertex(i, viewModel.verticesProperty().get(i));
        }

        // --- Roads geometry ---
        setupAllRoads(viewModel.getRoads());

        // --- Players ---
        ObservableList<PlayerViewState> players = viewModel.playersProperty();
        for (PlayerViewState player : players) {
            // optional: listen for name changes and update UI if needed
            player.nameProperty().addListener((obs, oldName, newName) -> { });
        }

        // --- Initialize UI with first current player ---
        if (!players.isEmpty()) {
            setCurrentPlayer(0);
        }

        // Make road 0 owned by player 1 (index 0 for red)
        if (!viewModel.roadsProperty().isEmpty()) {
            RoadViewState firstRoad = viewModel.roadsProperty().get(0);
            firstRoad.owner.set(0); // player 1 is index 0 → red
        }
    }

    private Label getLabelFromPane(StackPane pane) 
    {
        return pane.getChildren()
                .stream()
                .filter(n -> n instanceof Label)
                .map(n -> (Label) n)
                .findFirst()
                .orElseThrow(() ->
                    new IllegalStateException("StackPane does not contain a Label"));
    }

    private Polygon getBoxFromPane(StackPane pane) 
    {
        return pane.getChildren()
                .stream()
                .filter(n -> n instanceof Polygon)
                .map(n -> (Polygon) n)
                .findFirst()
                .orElseThrow(() ->
                    new IllegalStateException("StackPane does not contain a Polygon"));
    }

    private void assignPlayersToPanes(int currentPlayerIndex) 
    {
        ObservableList<PlayerViewState> players = viewModel.playersProperty();
        if (players.isEmpty()) return;

        currentPlayerIndex = currentPlayerIndex % players.size();

        // ---- CURRENT PLAYER ----
        PlayerViewState current = players.get(currentPlayerIndex);
        getLabelFromPane(currentPlayerPane).setText(current.nameProperty().get());

        Color currentColor = PLAYER_COLORS[currentPlayerIndex];
        bottomBackground.setFill(currentColor);
        bottomBackground.setStroke(Color.rgb(7, 4, 60));
        bottomBackground.setStrokeWidth(3);
        currentPlayerBox.setFill(currentColor);

        // ---- OTHER PLAYERS ----
        for (int i = 1; i <= 3; i++) {
            int playerIndex = (currentPlayerIndex + i) % players.size();
            PlayerViewState player = players.get(playerIndex);

            StackPane pane;
            switch (i) {
                case 1 -> pane = player1Pane;
                case 2 -> pane = player2Pane;
                default -> pane = player3Pane;
            }

            getLabelFromPane(pane).setText(player.nameProperty().get());
            getBoxFromPane(pane).setFill(PLAYER_COLORS[playerIndex]);
        }
    }

    private void bindTile(int index, TileViewState state) {
        state.number.addListener((obs, old, val) -> {
            setTile(index, val.intValue(), resolveColor(state.resource.get()));
        });

        setTile(index, state.number.get(), resolveColor(state.resource.get()));
    }

    private void bindVertex(int id, VertexViewState state) {
        state.type.addListener((obs, old, type) -> {
            setVertex(id, state.owner.get(), type.intValue());
        });

        setVertex(id, state.owner.get(), state.type.get());
    }

    private void bindRoad(int id, RoadViewState roadState) 
    {
        // Listen for changes to the road owner and update UI
        roadState.owner.addListener((obs, oldOwner, newOwner) -> {
            setRoad(id, newOwner.intValue());
        });

        // Initialize road with current owner
        setRoad(id, roadState.owner.get());
    }


    private Color resolveColor(String type) {
        return switch (type) {
            case "tile.forest" -> Color.FORESTGREEN;
            case "tile.hills" -> Color.ORANGERED;
            case "tile.desert" -> Color.rgb(198, 170, 71);
            case "tile.mountains" -> Color.GRAY;
            case "tile.fields" -> Color.GOLD;
            case "tile.pasture" -> Color.LIGHTGREEN;
            default -> Color.LIGHTGRAY;
        };
    }

    @FXML
    public void initialize() {

        // Load Oswald font from classpath
        InputStream fontStream = getClass().getResourceAsStream("/fonts/Oswald-Regular.ttf");
        if (fontStream != null) {
            oswaldFont = Font.loadFont(fontStream, 20); // set font size
            System.out.println("Oswald font loaded successfully.");
        } else {
            System.out.println("Failed to load Oswald font, using default.");
            oswaldFont = Font.font(20);
        }
                
        Platform.runLater(() -> {
            // Full width
            bottomBackground.widthProperty().bind(rootPane.widthProperty());

            // Half height
            bottomBackground.heightProperty().bind(rootPane.heightProperty().divide(2));

            // Position at halfway point
            bottomBackground.yProperty().bind(rootPane.heightProperty().divide(2));

            // Set fill AFTER sizing
            bottomBackground.setFill(PLAYER_COLORS[0]);
            currentPlayerBox.setFill(PLAYER_COLORS[0]);

            bottomBackground.toBack();
        });

        mainPentagon.setTranslateX(-rootPane.getWidth() / 2);
        mainPentagon.setTranslateY(-rootPane.getHeight() / 2);

        createPlayerNames();

        // Defer setting the current player until UI is ready
        Platform.runLater(() -> {
            if (!viewModel.playersProperty().isEmpty()) {
                setCurrentPlayer(3);
            }
        });

        System.out.println("GameScreenV initialized"); // Debug

        createCatanBoard(rootPane);

        addPlayerSwitchButtons();
    }

    private void mapPortsToVertices(int[][] ports)
    {
        Arrays.fill(vertexToPort, -1);

        for (int portId = 0; portId < ports.length; portId++) {
            int v1 = ports[portId][0];
            int v2 = ports[portId][1];

            vertexToPort[v1] = portId;
            vertexToPort[v2] = portId;
        }
    }


    private Group[] tileGroup = new Group[19];

    private Group createTile(double width, double height, int numberToken, Color resourceColor) {
        Group tile = new Group();

        // --- MAIN HEX ---
        Polygon hex = createHex(width, height);
        hex.setFill(resourceColor);
        hex.setStroke(Color.rgb(36, 31, 14));
        hex.setStrokeWidth(3);

        // --- NUMBER TOKEN ---
        String displayStr;
        if (numberToken == 0) {
            displayStr = "";
        } else if (numberToken == 1) {
            displayStr = "R";
        } else {
            displayStr = String.valueOf(numberToken);
        }

        Text outlineText = new Text(displayStr);
        outlineText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Oswald-Regular.ttf"), 50));
        outlineText.setFill(Color.BLACK);
        outlineText.setStroke(Color.BLACK);
        outlineText.setStrokeWidth(6);
        outlineText.setTextOrigin(VPos.CENTER);

        Text numberText = new Text(displayStr);
        numberText.setFont(outlineText.getFont());
        numberText.setFill(Color.WHITE);
        numberText.setTextOrigin(VPos.CENTER);

        StackPane numberPane = new StackPane(outlineText, numberText);
        numberPane.setPrefSize(width, height - 30);
        numberPane.setMouseTransparent(true);

        tile.getChildren().addAll(hex, numberPane);

        // --- BORDER HEX in borderPane ---
        double borderScale = 1.6;
        Polygon borderHex = createHex(width * borderScale, height * borderScale);
        borderHex.setFill(Color.rgb(236, 210, 114));

        // Center border hex behind the tile
        borderHex.layoutXProperty().bind(
                tile.layoutXProperty()
                        .subtract((borderHex.getBoundsInLocal().getWidth() - width) / 2));

        borderHex.layoutYProperty().bind(
                tile.layoutYProperty()
                        .subtract((borderHex.getBoundsInLocal().getHeight() - height) / 2));

        borderPane.getChildren().add(borderHex);

        return tile;
    }

    private void setupAllVertices(int[][] tileVertices) {
        vertexPane.toFront(); // Ensure vertex layer is above everything

        // Map vertexId -> list of hexes it belongs to
        Map<Integer, List<Group>> vertexHexMap = new HashMap<>();
        Map<Integer, List<double[]>> vertexLocalCorners = new HashMap<>();

        // Step 1: Collect hexes and local corner positions
        for (int tileIndex = 0; tileIndex < tileVertices.length; tileIndex++) {
            Group tile = tileGroup[tileIndex];
            if (tile == null)
                continue;

            Polygon hex = (Polygon) tile.getChildren().get(0);
            ObservableList<Double> pts = hex.getPoints();

            // Calculate hex center
            double centerX = 0, centerY = 0;
            for (int i = 0; i < 6; i++) {
                centerX += pts.get(i * 2);
                centerY += pts.get(i * 2 + 1);
            }
            centerX /= 6;
            centerY /= 6;

            for (int i = 0; i < 6; i++) {
                int vertexId = tileVertices[tileIndex][i];
                double cornerX = pts.get(i * 2);
                double cornerY = pts.get(i * 2 + 1);

                vertexHexMap.computeIfAbsent(vertexId, k -> new ArrayList<>()).add(tile);
                vertexLocalCorners.computeIfAbsent(vertexId, k -> new ArrayList<>())
                        .add(new double[] { cornerX, cornerY, centerX, centerY });
            }
        }

        // Step 2: Place each vertex
        for (int vertexId = 0; vertexId < vertexNodes.length; vertexId++) {
            if (!vertexLocalCorners.containsKey(vertexId))
                continue;

            List<double[]> corners = vertexLocalCorners.get(vertexId);
            double avgX = 0, avgY = 0;

            for (double[] data : corners) {
                double cornerX = data[0];
                double cornerY = data[1];
                double centerX = data[2];
                double centerY = data[3];

                // Vector from hex center to corner
                double dx = cornerX - centerX;
                double dy = cornerY - centerY;

                // Push outward a little
                double pushFactor = 0.25;
                double pushedX = cornerX + dx * pushFactor;
                double pushedY = cornerY + dy * pushFactor;

                // Convert to vertexPane coordinates
                Group tile = vertexHexMap.get(vertexId).get(corners.indexOf(data));
                double sceneX = tile.localToScene(pushedX, pushedY).getX() - vertexPane.getLayoutX();
                double sceneY = tile.localToScene(pushedX, pushedY).getY() - vertexPane.getLayoutY();

                avgX += sceneX;
                avgY += sceneY;
            }

            avgX /= corners.size();
            avgY /= corners.size();

            // Create vertex shape (default Circle for now)
            Shape vertex = new Circle(7);
            vertex.setFill(Color.GREY);
            vertex.setStroke(Color.WHITE);
            vertex.setStrokeWidth(2);
            vertex.setLayoutX(avgX);
            vertex.setLayoutY(avgY);

            vertexNodes[vertexId] = vertex;
            vertexPane.getChildren().add(vertex);

            // // Add vertex ID label above
            // Text label = new Text(String.valueOf(vertexId));
            // label.setFill(Color.INDIGO);
            // label.setFont(Font.font(12));
            // label.setLayoutX(avgX - 4);
            // label.setLayoutY(avgY - 12);
            // vertexPane.getChildren().add(label);
        }
    }

    private void setupAllRoads(int[][] roadVertices) 
    {
        roadsPane.toFront(); // Ensure road layer is above everything

        if (roadVertices == null || roadVertices.length == 0 || vertexNodes == null) return;

        roadNodes = new Shape[roadVertices.length];

        double shorten = 16; // pixels to shorten at each end

        for (int roadId = 0; roadId < roadVertices.length; roadId++) {
            int v1 = roadVertices[roadId][0];
            int v2 = roadVertices[roadId][1];

            Shape vertex1 = vertexNodes[v1];
            Shape vertex2 = vertexNodes[v2];

            if (vertex1 == null || vertex2 == null) continue;

            double x1 = vertex1.getLayoutX();
            double y1 = vertex1.getLayoutY();
            double x2 = vertex2.getLayoutX();
            double y2 = vertex2.getLayoutY();

            // Compute full vector
            double dx = x2 - x1;
            double dy = y2 - y1;
            double fullLength = Math.hypot(dx, dy);

            // Normalize vector
            double ux = dx / fullLength;
            double uy = dy / fullLength;

            // Shorten both ends
            double startX = x1 + ux * shorten;
            double startY = y1 + uy * shorten;
            double endX = x2 - ux * shorten;
            double endY = y2 - uy * shorten;

            // Midpoint for rectangle placement
            double midX = (startX + endX) / 2;
            double midY = (startY + endY) / 2;

            // New length
            double roadLength = Math.hypot(endX - startX, endY - startY);

            // Rotation angle
            double angle = Math.toDegrees(Math.atan2(endY - startY, endX - startX));

            // Create rectangle
            double roadWidth = 7;
            Rectangle road = new Rectangle(roadLength, roadWidth);
            road.setFill(Color.GRAY);
            road.setStroke(Color.BLACK);
            road.setStrokeWidth(1);

            // Center rectangle on midpoint
            road.setTranslateX(midX - roadLength / 2);
            road.setTranslateY(midY - roadWidth / 2);
            road.setRotate(angle);

            roadNodes[roadId] = road;
            roadsPane.getChildren().add(road);
        }
    }

    private Group createPips(int numberToken) {
        Group pips = new Group();

        int dotCount = switch (numberToken) {
            case 2, 12 -> 1;
            case 3, 11 -> 2;
            case 4, 10 -> 3;
            case 5, 9 -> 4;
            case 6, 8 -> 5;
            default -> 0;
        };

        double spacing = 16;
        double startX = -(dotCount - 1) * spacing / 2;

        for (int i = 0; i < dotCount; i++) {
            Circle dot = new Circle(6);
            dot.setFill(numberToken == 6 || numberToken == 8 ? Color.RED : Color.WHITE);

            dot.setStroke(Color.BLACK);
            dot.setStrokeWidth(2);

            dot.setLayoutX((startX + i * spacing) - 2);
            pips.getChildren().add(dot);
        }

        return pips;
    }

    private void setTile(int index, int numberToken, Color resourceColor) {
        if (!isValidIndex(index)) {
            return;
        }

        Group tile = tileGroup[index];

        Polygon hex = (Polygon) tile.getChildren().get(0);
        StackPane numberPane = (StackPane) tile.getChildren().get(1);

        Text outlineText = (Text) numberPane.getChildren().get(0);
        Text numberText = (Text) numberPane.getChildren().get(1);

        // Update main hex color
        hex.setFill(resourceColor);

        // Update number token
        String tokenStr;
        if (numberToken == 0) {
            tokenStr = "";
        } else if (numberToken == 1) {
            tokenStr = "R";
        } else {
            tokenStr = String.valueOf(numberToken);
        }

        outlineText.setText(tokenStr);
        numberText.setText(tokenStr);
        numberText.setFill(numberToken == 6 || numberToken == 8 ? Color.RED : Color.WHITE);

        // Remove old pips (keep border hex and number pane)
        tile.getChildren().removeIf(n -> n instanceof Group && n != tile.getChildren().get(1) && n != numberPane);

        // Add new pips
        Group pips = createPips(numberToken);
        Bounds b = hex.getBoundsInLocal();
        pips.setLayoutX(b.getWidth() / 2);
        pips.setLayoutY(b.getHeight() / 2 + 18);
        tile.getChildren().add(pips);
    }

    private void setVertex(int vertexId, int playerOwner, int contains)
    {
        if (vertexId < 0 || vertexId >= vertexNodes.length || vertexNodes[vertexId] == null) {
            return;
        }

        double x = vertexNodes[vertexId].getLayoutX();
        double y = vertexNodes[vertexId].getLayoutY();

        vertexPane.getChildren().remove(vertexNodes[vertexId]);

        Color[] playerColors = {
            Color.GRAY,
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.YELLOW
        };

        Color fillColor = (playerOwner >= 0 && playerOwner < playerColors.length)
                ? playerColors[playerOwner]
                : Color.GRAY;

        Shape baseNode;

        if (contains == 1) {
            double size = 25;
            Rectangle city = new Rectangle(size, size);
            city.setFill(fillColor);
            city.setStroke(Color.BLACK);
            city.setStrokeWidth(2);
            city.setLayoutX(x - size / 2);
            city.setLayoutY(y - size / 2);
            baseNode = city;
        } else {
            double radius = 10;
            Circle settlement = new Circle(radius);
            settlement.setFill(fillColor);
            settlement.setStroke(Color.BLACK);
            settlement.setStrokeWidth(2);
            settlement.setLayoutX(x);
            settlement.setLayoutY(y);
            baseNode = settlement;
        }

        vertexNodes[vertexId] = baseNode;
        vertexPane.getChildren().add(baseNode);

        // Remove old decoration if present
        if (portDecorations[vertexId] != null) {
            vertexPane.getChildren().remove(portDecorations[vertexId]);
            portDecorations[vertexId] = null;
        }

        if (vertexToPort[vertexId] != -1) {
            Circle portRing = new Circle(15);
            portRing.setFill(Color.TRANSPARENT);
            portRing.setStroke(Color.CRIMSON);
            portRing.setStrokeWidth(3);
            portRing.setLayoutX(x);
            portRing.setLayoutY(y);
            portRing.setMouseTransparent(true);

            portDecorations[vertexId] = portRing;
            vertexPane.getChildren().add(portRing);
        }
    }


    private void setRoad(int roadId, int playerOwner) 
    {
        if (roadId < 0 || roadId >= roadNodes.length || roadNodes[roadId] == null) {
            return;
        }

        Color[] playerColors = { Color.GRAY, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW };
        Color fillColor = (playerOwner >= 0 && playerOwner < playerColors.length) 
                        ? playerColors[playerOwner] 
                        : Color.GRAY;

        Shape roadShape = roadNodes[roadId];
        if (roadShape instanceof Rectangle rect) {
            rect.setFill(fillColor);
        }
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < tileGroup.length && tileGroup[index] != null;
    }

    public void highlightTile(int index, boolean highlight) {
        if (!isValidIndex(index))
            return;

        Platform.runLater(() -> {
            Polygon hex = (Polygon) tileGroup[index].getChildren().get(0);
            hex.setStroke(highlight ? Color.GOLD : Color.WHITE);
            hex.setStrokeWidth(highlight ? 4 : 2);
        });
    }

    public void setTileDisabled(int index, boolean disabled) {
        if (!isValidIndex(index))
            return;

        Platform.runLater(() -> {
            tileGroup[index].setOpacity(disabled ? 1.0 : 1.0);
            tileGroup[index].setMouseTransparent(disabled);
        });
    }

    private void createCatanBoard(Pane boardPane) {
        double hexWidth = 120;
        double hexHeight = 120;
        double gap = 34;
        double rightShift = 100;
        double totalHeight = 864;

        int[] rowHexCounts = { 3, 4, 5, 4, 3 };

        int centerRowIndex = 2;
        double centerRowY = totalHeight / 2;
        double middleHexY = centerRowIndex * (0.75 * hexHeight + gap) + (hexHeight / 2);
        double verticalOffset = centerRowY - middleHexY;

        int id = 0;

        // --- Background ---
        Polygon background = createFlatTopHex(hexWidth * 8.2, totalHeight - 40);

        // I Will want to bring this back at some point, just having a bit of a layering issue
        background.setFill(Color.rgb(57, 69, 147));
        background.setStroke(Color.rgb(7, 4, 60));
        background.setStrokeWidth(3);
        boardPane.getChildren().add(background);

        background.toBack();
        mainPentagon.toBack();

        // --- Create tiles ---
        for (int row = 0; row < rowHexCounts.length; row++) {

            int count = rowHexCounts[row];
            double y = verticalOffset + row * (0.75 * hexHeight + gap);

            for (int col = 0; col < count; col++) {

                double x = col * (hexWidth + gap);

                if (row % 2 == 1) {
                    x += (hexWidth + gap) / 2;
                }

                if (row == 0 || row == rowHexCounts.length - 1) {
                    x += hexWidth + gap;
                }

                x += rightShift;

                // ---- TILE CREATION ----
                Group tile = createTile(
                        hexWidth,
                        hexHeight,
                        0, // placeholder number token
                        Color.LIGHTGRAY // placeholder resource
                );

                tile.setLayoutX(x);
                tile.setLayoutY(y);

                tileGroup[id] = tile;
                boardPane.getChildren().add(tile);

                id++;
            }
        }

        // --- Center background on center tile (index 9) ---
        Bounds centerTileBounds = tileGroup[9].getBoundsInParent();
        Bounds bgBounds = background.getBoundsInLocal();

        double centerX = centerTileBounds.getMinX() + centerTileBounds.getWidth() / 2;
        double centerY = centerTileBounds.getMinY() + centerTileBounds.getHeight() / 2;

        background.setLayoutX(centerX - bgBounds.getWidth() / 2);
        background.setLayoutY(centerY - bgBounds.getHeight() / 2 + 21);

        System.out.println("Catan board created with 19 tile views.");
    }

    private Polygon createHex(double width, double height) {
        Polygon hex = new Polygon();
        double w = width;
        double h = height;
        hex.getPoints().addAll(
                w / 2, 0.0,
                w, h / 4,
                w, 3 * h / 4,
                w / 2, h,
                0.0, 3 * h / 4,
                0.0, h / 4);
        return hex;
    }

    private Polygon createFlatTopHex(double width, double height) {
        Polygon hex = new Polygon();
        hex.getPoints().addAll(
                width / 4, 0.0, // top-left
                3 * width / 4, 0.0, // top-right
                width, height / 2, // right
                3 * width / 4, height, // bottom-right
                width / 4, height, // bottom-left
                0.0, height / 2 // left
        );
        return hex;
    }

    public void setCurrentPlayer(int currentPlayerIndex) 
    {
        // then update UI
        assignPlayersToPanes(currentPlayerIndex);
    }

    private void setPaneColor(StackPane pane, Color color) 
    {
        pane.setBackground(new javafx.scene.layout.Background(
            new javafx.scene.layout.BackgroundFill(
                color,
                new javafx.scene.layout.CornerRadii(8),
                javafx.geometry.Insets.EMPTY
            )
        ));
    }

    private void createPlayerNames()
    {
        Platform.runLater(() -> {

            double centerY = rootPane.getHeight() / 2.0;
            double currentHeight = currentPlayerPane.getBoundsInParent().getHeight();

            double currentYOffset = 90;   // ↓ move current player down
            double normalGap = 85;
            double firstGap  = 140;
            double slantX = -50;

            currentPlayerPane.setLayoutX(-50);
            currentPlayerPane.setLayoutY(centerY - currentHeight / 2 + currentYOffset);
            currentPlayerText.setLayoutX(10);
            currentPlayerText.setLayoutY(centerY - currentHeight / 2 + currentYOffset - 35);

            player1Pane.setLayoutY(currentPlayerPane.getLayoutY() - firstGap);
            player1Pane.setLayoutX(slantX + 10);

            player2Pane.setLayoutY(player1Pane.getLayoutY() - normalGap);
            player2Pane.setLayoutX(slantX * 2 + 10);

            player3Pane.setLayoutY(player2Pane.getLayoutY() - normalGap);
            player3Pane.setLayoutX(slantX * 3 + 10);
        });
    }

    // TEMP TEST CODE
    private void addPlayerSwitchButtons() {
        Platform.runLater(() -> {
            double buttonWidth = 80;
            double buttonHeight = 30;
            double startX = 20;
            double startY = 20;
            double gap = 10;

            for (int i = 0; i < 4; i++) {
                int playerIndex = i;
                javafx.scene.control.Button btn = new javafx.scene.control.Button("Player " + (i + 1));
                btn.setLayoutX(startX);
                btn.setLayoutY(startY + i * (buttonHeight + gap));
                btn.setPrefSize(buttonWidth, buttonHeight);

                btn.setOnAction(e -> setCurrentPlayer(playerIndex));

                rootPane.getChildren().add(btn);
            }
        });
    }

}
