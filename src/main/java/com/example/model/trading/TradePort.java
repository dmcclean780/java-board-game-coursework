package com.example.model.trading;
import com.example.model.config.PortConfig;
import com.example.model.config.ResourceConfig;

// TradePort represents a trade between a player and a port, where the player gives a certain resource to receive another
public record TradePort(PortConfig port, int playerId, ResourceConfig resource) {}
