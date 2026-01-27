









package com.example.model.config;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TileConfig extends IdentifiableConfig {
    public final String texturePath;

    public final boolean hasRobberDefault;
    public final String resourceID; // -1 for none
    public final int maxQuantity;

    public TileConfig(
            @JsonProperty("id") String id, 
            @JsonProperty("texturePath") String texturePath, 
            @JsonProperty("resourceID") String resourceID,
            @JsonProperty("hasRobberDefault") boolean hasRobberDefault, 
            @JsonProperty("maxQuantity") int maxQuantity) {
        this.id = id;
        this.texturePath = texturePath;
        this.resourceID = resourceID;
        this.hasRobberDefault = hasRobberDefault;
        this.maxQuantity = maxQuantity;
    }

}
