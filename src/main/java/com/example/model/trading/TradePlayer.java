package com.example.model.trading;

import com.example.model.config.ResourceConfig;

// TradePlayer represents a trade between two players, where each player gives a certain amount of a resource to the other
public record TradePlayer(int playerAId, int playerBId, ResourceConfig resourceAGive, int amountA, ResourceConfig resourceBGive, int amountB) {}
