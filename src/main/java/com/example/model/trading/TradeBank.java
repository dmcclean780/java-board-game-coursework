package com.example.model.trading;
import com.example.model.config.ResourceConfig;

// TradeBank represents a trade between a player and the bank, where the player gives one type of resource
public record TradeBank(int playerId, ResourceConfig giveResource, ResourceConfig recieveResource) {
    public static final int TRADE_RATE = 4; // Saves me entering magic number multiple times
}
