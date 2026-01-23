package com.example.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DevCardConfig extends IdentifiableConfig {
    public final String id;
    public final String texturePath;

    public final String actionType;
    public final int count;

    @JsonCreator
    public DevCardConfig(
            @JsonProperty("id") String id,
            @JsonProperty("texturePath") String texturePath,
            @JsonProperty("actionType") String actionType, 
            @JsonProperty("count") int count) {
        this.id = id;
        this.texturePath = texturePath;
        this.actionType = actionType;
        this.count = count;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
