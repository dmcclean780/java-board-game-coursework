package com.example.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public final class RoadViewState {
    public final IntegerProperty owner = new SimpleIntegerProperty(-1);
    public final BooleanProperty visible = new SimpleBooleanProperty(false);
}

