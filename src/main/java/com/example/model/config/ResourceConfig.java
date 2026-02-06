package com.example.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceConfig extends IdentifiableConfig {
    public final String texturePath;
    public final String symbol;
    public final int maxQuantity;
    public final String colorHex;

    @JsonCreator
    public ResourceConfig(
            @JsonProperty("id") String id,
            @JsonProperty("symbol") String symbol,
            @JsonProperty("texturePath") String texturePath,
            @JsonProperty("maxQuantity") int maxQuantity,
            @JsonProperty("colorHex") String colorHex) {
        this.id = id;
        this.symbol = symbol;
        this.texturePath = texturePath;
        this.maxQuantity = maxQuantity;
        this.colorHex = colorHex;
    }
}
