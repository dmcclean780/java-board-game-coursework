package com.example.view;

import com.example.viewmodel.ResourceViewState;

import com.example.model.config.LangManager;
import com.example.model.config.ResourceConfig;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Polygon;
import javafx.beans.binding.Bindings;
import javafx.scene.paint.Paint;

public class ResourceBoxController {

        @FXML
        private Label resourceSymbol;
    @FXML
    private Label resourceName;
    @FXML
    private Label resourceCount;
    @FXML
    private Polygon resourcePolygon;

    public void bind(ResourceViewState resource) {
        resourceSymbol.textProperty().bind(
                Bindings.createStringBinding(
                        () -> resource.configProperty().get().symbol,
                        resource.configProperty()));
                        
        resourceName.textProperty().bind(
                Bindings.createStringBinding(
                        () -> LangManager.get(resource.configProperty().get().id + ".name"),
                        resource.configProperty()));
        resourceCount.textProperty().bind(resource.countProperty().asString());
        resourcePolygon.fillProperty().bind(
                Bindings.createObjectBinding(
                        () -> Paint.valueOf(
                                resource.configProperty().get().colorHex),
                        resource.configProperty()));
    }
}
