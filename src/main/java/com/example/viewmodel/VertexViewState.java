package com.example.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public final class VertexViewState {
    public final IntegerProperty owner = new SimpleIntegerProperty(-1);
    public final IntegerProperty type = new SimpleIntegerProperty(0); // 0=none,1=settlement,2=city
}

