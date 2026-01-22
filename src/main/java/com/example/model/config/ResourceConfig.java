package com.example.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ResourceConfig implements IdentifiableConfig {
    public final String id;
    public final String texturePath;

    public final int maxQuantity;

    @JsonCreator
    public ResourceConfig(
            @JsonProperty("id") String id,
            @JsonProperty("texturePath") String texturePath,
            @JsonProperty("maxQuantity") int maxQuantity) {
        this.id = id;
        this.texturePath = texturePath;
        this.maxQuantity = maxQuantity;
    }

    @Override
    public String getId() {
        return this.id; 
    }
}
