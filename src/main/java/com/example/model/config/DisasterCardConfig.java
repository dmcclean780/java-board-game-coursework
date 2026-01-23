package com.example.model.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DisasterCardConfig extends IdentifiableConfig {
    public final String id;
    public final String texturePath;

    public final String tileAffected;
    public final int count;

    @JsonCreator
    public DisasterCardConfig(
            @JsonProperty("id") String id,
            @JsonProperty("texturePath") String texturePath,
            @JsonProperty("tileAffected") String tileAffected, 
            @JsonProperty("count") int count) {
        this.id = id;
        this.texturePath = texturePath;
        this.tileAffected = tileAffected;
        this.count = count;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
