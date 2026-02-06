package com.example.viewmodel;

import com.example.model.config.ResourceConfig;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ResourceViewState {
    private final ObjectProperty<ResourceConfig> config = new SimpleObjectProperty<>();
    private final IntegerProperty count = new SimpleIntegerProperty();

    public ObjectProperty<ResourceConfig> configProperty() {
        return config;
    }

    public IntegerProperty countProperty() {
        return count;
    }
}
