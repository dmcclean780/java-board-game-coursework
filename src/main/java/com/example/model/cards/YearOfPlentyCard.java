package com.example.model.cards;
import com.example.model.cards.interfaces.CardAction;
import com.example.model.GameModel;

public class YearOfPlentyCard implements CardAction {

    @Override
    public void execute(GameModel model, int playerID) {
        // gives choice of any 2 resources to the player
        // adds those resources to the player's resource list
    }
}

