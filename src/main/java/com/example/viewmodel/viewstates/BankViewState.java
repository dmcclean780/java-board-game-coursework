package com.example.viewmodel.viewstates;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BankViewState {
    private final ObservableList<ResourceViewState> resources = FXCollections.observableArrayList();

    public ObservableList<ResourceViewState> getResources() { return resources; }

}
