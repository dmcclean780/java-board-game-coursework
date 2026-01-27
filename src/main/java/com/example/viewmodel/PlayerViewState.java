package com.example.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PlayerViewState {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final BooleanProperty canBuildSettlement = new SimpleBooleanProperty();
    private final BooleanProperty canBuildCity = new SimpleBooleanProperty();
    private final BooleanProperty canBuildRoad = new SimpleBooleanProperty();

    public StringProperty nameProperty() { return name; }
    public IntegerProperty idProperty() { return id; }
    public BooleanProperty canBuildSettlementProperty() { return canBuildSettlement; }
    public BooleanProperty canBuildCityProperty() { return canBuildCity; }
    public BooleanProperty canBuildRoadProperty() { return canBuildRoad; }
}
