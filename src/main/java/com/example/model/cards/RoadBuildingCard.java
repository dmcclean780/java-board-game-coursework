package com.example.model.cards;

import com.example.model.GameModel;
import com.example.model.cards.interfaces.CardAction;

public class RoadBuildingCard implements CardAction {

    @Override
    public void execute(GameModel model, int playerID) {
        // allows the player to build 2 roads for free
    }
    
}
