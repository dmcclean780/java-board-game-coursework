package com.example.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DiceViewState {
    private final IntegerProperty dice1 = new SimpleIntegerProperty();
    private final IntegerProperty dice2 = new SimpleIntegerProperty();

    public IntegerProperty dice1Property() { return dice1; }
    public IntegerProperty dice2Property() { return dice2; }
}
