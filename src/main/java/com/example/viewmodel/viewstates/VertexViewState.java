package com.example.viewmodel.viewstates;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

public final class VertexViewState {
    public final IntegerProperty owner = new SimpleIntegerProperty(-1);
    public final StringProperty type = new SimpleStringProperty(); // "Settlement" or "City"
    public final BooleanProperty visible = new SimpleBooleanProperty(true);

}

