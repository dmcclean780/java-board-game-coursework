package com.example.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.viewmodel.GameScreenVM;

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

public class GameScreenV 
{
    @FXML
    private Font oswaldFont;

    @FXML
    private Pane vertexPane;

    @FXML
    private Pane borderPane;  

    @FXML
    private Polygon mainPentagon;

    @FXML
    private Pane rootPane, catanBoardPane;

    @FXML
    private Label player1Display, player2Display, player3Display, currentPlayerDisplay;

    // Static holder for names before screen loads
    private static String[] playerNames;

    private Shape[] vertexNodes = new Shape[54]; // can hold Circle or Rectangle

    // Called from Setup screen before switching
    public static void setNextPlayerNames(String[] names) {
        playerNames = names;
    }

    private Group[] tileGroup = new Group[19];

    private Group createTile(double width, double height, int numberToken, Color resourceColor) 
    {
        Group tile = new Group();

        // --- MAIN HEX ---
        Polygon hex = createHex(width, height);
        hex.setFill(resourceColor);
        hex.setStroke(Color.rgb(88, 76, 35));
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
                .subtract((borderHex.getBoundsInLocal().getWidth() - width) / 2)
        );

        borderHex.layoutYProperty().bind(
            tile.layoutYProperty()
                .subtract((borderHex.getBoundsInLocal().getHeight() - height) / 2)
        );


        borderPane.getChildren().add(borderHex);

        return tile;
    }

    private void setupAllVertices(int[][] tileVertices) 
    {
        vertexPane.toFront(); // Ensure vertex layer is above everything

        // Map vertexId -> list of hexes it belongs to
        Map<Integer, List<Group>> vertexHexMap = new HashMap<>();
        Map<Integer, List<double[]>> vertexLocalCorners = new HashMap<>();

        // Step 1: Collect hexes and local corner positions
        for (int tileIndex = 0; tileIndex < tileVertices.length; tileIndex++) {
            Group tile = tileGroup[tileIndex];
            if (tile == null) continue;

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
                vertexLocalCorners.computeIfAbsent(vertexId, k -> new ArrayList<>()).add(new double[]{cornerX, cornerY, centerX, centerY});
            }
        }

        // Step 2: Place each vertex
        for (int vertexId = 0; vertexId < vertexNodes.length; vertexId++) {
            if (!vertexLocalCorners.containsKey(vertexId)) continue;

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
            Shape vertex = new Circle(8);
            vertex.setFill(Color.GREY);
            vertex.setStroke(Color.WHITE);
            vertex.setStrokeWidth(1);
            vertex.setLayoutX(avgX);
            vertex.setLayoutY(avgY);

            vertexNodes[vertexId] = vertex;
            vertexPane.getChildren().add(vertex);

            // Add vertex ID label above
            // Text label = new Text(String.valueOf(vertexId));
            // label.setFill(Color.PINK);
            // label.setFont(Font.font(12));
            // label.setLayoutX(avgX - 4);
            // label.setLayoutY(avgY - 12);
            // vertexPane.getChildren().add(label);
        }
    }

    private Group createPips(int numberToken) 
    {
        Group pips = new Group();

        int dotCount = switch (numberToken) {
            case 2, 12 -> 1;
            case 3, 11 -> 2;
            case 4, 10 -> 3;
            case 5, 9 -> 4;
            case 6, 8 -> 5;
            default -> 0;
        };

        double spacing = 14;
        double startX = -(dotCount - 1) * spacing / 2;

        for (int i = 0; i < dotCount; i++) {
            Circle dot = new Circle(6);
            dot.setFill(numberToken == 6 || numberToken == 8 ? Color.RED : Color.WHITE);

            dot.setStroke(Color.BLACK);
            dot.setStrokeWidth(2);

            dot.setLayoutX((startX + i * spacing) - 2);
            pips.getChildren().add(dot);
        }

        // Center the pips group
        //pips.setLayoutX(120 / 2); // half hex width
        //pips.setLayoutY(120 / 2 + 18); // vertical offset like before

        return pips;
    }

    public void setTile(int index, int numberToken, Color resourceColor) 
    {
        
        if (!isValidIndex(index)) return;

        Platform.runLater(() -> {
            Group tile = tileGroup[index];

            Polygon hex = (Polygon) tile.getChildren().get(0);
            StackPane numberPane = (StackPane) tile.getChildren().get(1);

            Text outlineText = (Text) numberPane.getChildren().get(0);
            Text numberText  = (Text) numberPane.getChildren().get(1);

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
        });
    }


    public void setVertex(int vertexId, int playerOwner, int contains) 
    {
        if (vertexId < 0 || vertexId >= vertexNodes.length || vertexNodes[vertexId] == null) return;

        Platform.runLater(() -> {
            // Remove old node (circle or rectangle)
            vertexPane.getChildren().remove(vertexNodes[vertexId]);

            // Determine color based on owner
            Color[] playerColors = {Color.GRAY, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
            Color fillColor = (playerOwner >= 0 && playerOwner < playerColors.length) ? playerColors[playerOwner] : Color.GRAY;

            // Create new node depending on type
            if (contains == 1) { // city = square
                double size = 25;
                Rectangle city = new Rectangle(size, size);
                city.setFill(fillColor);
                city.setStroke(Color.BLACK);
                city.setStrokeWidth(2);
                // Center rectangle on vertex coordinates
                city.setLayoutX(vertexNodes[vertexId].getLayoutX() - size / 2);
                city.setLayoutY(vertexNodes[vertexId].getLayoutY() - size / 2);
                vertexNodes[vertexId] = city;
                vertexPane.getChildren().add(city);
            } else { // settlement = circle
                double radius = 12;
                Circle settlement = new Circle(radius);
                settlement.setFill(fillColor);
                settlement.setStroke(Color.BLACK);
                settlement.setStrokeWidth(2);
                settlement.setLayoutX(vertexNodes[vertexId].getLayoutX());
                settlement.setLayoutY(vertexNodes[vertexId].getLayoutY());
                vertexNodes[vertexId] = settlement;
                vertexPane.getChildren().add(settlement);
            }
        });
    }


    private boolean isValidIndex(int index) 
    {
        return index >= 0 && index < tileGroup.length && tileGroup[index] != null;
    }

    public void highlightTile(int index, boolean highlight) {
        if (!isValidIndex(index)) return;

        Platform.runLater(() -> {
            Polygon hex = (Polygon) tileGroup[index].getChildren().get(0);
            hex.setStroke(highlight ? Color.GOLD : Color.WHITE);
            hex.setStrokeWidth(highlight ? 4 : 2);
        });
    }

    public void setTileDisabled(int index, boolean disabled) 
    {
        if (!isValidIndex(index)) return;

        Platform.runLater(() -> {
            tileGroup[index].setOpacity(disabled ? 1.0 : 1.0);
            tileGroup[index].setMouseTransparent(disabled);
        });
    }

    @FXML
    public void initialize() 
    {

        // Load Oswald font from classpath
        InputStream fontStream = getClass().getResourceAsStream("/fonts/Oswald-Regular.ttf");
        if (fontStream != null) {
            oswaldFont = Font.loadFont(fontStream, 20); // set font size
            System.out.println("Oswald font loaded successfully.");
        } else {
            System.out.println("Failed to load Oswald font, using default.");
            oswaldFont = Font.font(20);
        }

        mainPentagon.setTranslateX(-rootPane.getWidth()/2);
        mainPentagon.setTranslateY(-rootPane.getHeight()/2);

        System.out.println("GameScreenV initialized"); // Debug
        if (playerNames != null) {
            player1Display.setText(playerNames[0]);
            player2Display.setText(playerNames[1]);
            player3Display.setText(playerNames[2]);
            currentPlayerDisplay.setText(playerNames[3] != null ? playerNames[3] : "N/A");
            System.out.println("Player names set.");
            playerNames = null;
        } else {
            System.out.println("playerNames is null");
        }

        createCatanBoard(rootPane);
        
        GameScreenVM vm = new GameScreenVM(this);
        vm.pushInitialStateToView();

        vm.updateVertex(36, 1, 0);
        vm.updateVertex(40, 1, 1);

        vm.updateVertex(12, 2, 1);
        vm.updateVertex(24, 2, 0);

        vm.updateVertex(3, 3, 0);
        vm.updateVertex(50, 3, 1);

        vm.updateVertex(0, 4, 1);
        vm.updateVertex(29, 4, 1);

        // Get the vertices mapping
        int[][] tileVertices = vm.getTileVertices();

        // Now setup vertices for all tiles
        setupAllVertices(tileVertices);
    }

    private void createCatanBoard(Pane boardPane) 
    {
        double hexWidth = 120;
        double hexHeight = 120;
        double gap = 25;
        double rightShift = 125;
        double totalHeight = 864;

        int[] rowHexCounts = {3, 4, 5, 4, 3};

        int centerRowIndex = 2;
        double centerRowY = totalHeight / 2;
        double middleHexY = centerRowIndex * (0.75 * hexHeight + gap) + (hexHeight / 2);
        double verticalOffset = centerRowY - middleHexY;

        int id = 0;

        // --- Background ---
        Polygon background = createFlatTopHex(hexWidth * 7.60, totalHeight - 130);

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
                        0,                 // placeholder number token
                        Color.LIGHTGRAY    // placeholder resource
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
        background.setLayoutY(centerY - bgBounds.getHeight() / 2 + 12);

        System.out.println("Catan board created with 19 tile views.");
    }

    private Polygon createHex(double width, double height) {
        Polygon hex = new Polygon();
        double w = width;
        double h = height;
        hex.getPoints().addAll(
            w/2, 0.0,
            w, h/4,
            w, 3*h/4,
            w/2, h,
            0.0, 3*h/4,
            0.0, h/4
        );
        return hex;
    }

    private Polygon createFlatTopHex(double width, double height) 
    {
        Polygon hex = new Polygon();
        hex.getPoints().addAll(
            width / 4, 0.0,         // top-left
            3 * width / 4, 0.0,     // top-right
            width, height / 2,      // right
            3 * width / 4, height,  // bottom-right
            width / 4, height,      // bottom-left
            0.0, height / 2         // left
        );
        return hex;
    }
}
