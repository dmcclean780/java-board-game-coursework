package com.example.viewmodel.viewstates;

import com.example.model.config.PortConfig;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlayerViewState {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty score = new SimpleIntegerProperty();
    private final BooleanProperty longestRoad = new SimpleBooleanProperty();
    private final BooleanProperty cleanestEnvironment = new SimpleBooleanProperty();
    private final BooleanProperty canBuildSettlement = new SimpleBooleanProperty();
    private final BooleanProperty canBuildCity = new SimpleBooleanProperty();
    private final BooleanProperty canBuildRoad = new SimpleBooleanProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();

    private final ObservableList<ResourceViewState> resources = FXCollections.observableArrayList();
    private final ObservableList<PortConfig> ports = FXCollections.observableArrayList();


    public StringProperty nameProperty() { return name; }
    public IntegerProperty idProperty() { return id; }
    public IntegerProperty scoreProperty() { return score; }
    public BooleanProperty longestRoadProperty() { return longestRoad; }
    public BooleanProperty cleanestEnvironmentProperty() { return cleanestEnvironment; }
    public BooleanProperty canBuildSettlementProperty() { return canBuildSettlement; }
    public BooleanProperty canBuildCityProperty() { return canBuildCity; }
    public BooleanProperty canBuildRoadProperty() { return canBuildRoad; }
    public ObjectProperty<Color> colorProperty() { return color; }
    public ObservableList<ResourceViewState> getResources() { return resources; }
    public ObservableList<PortConfig> getPorts() { return ports; }
}