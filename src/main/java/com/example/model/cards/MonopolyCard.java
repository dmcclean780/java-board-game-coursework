package com.example.model.cards;
import com.example.model.GameModel;
import com.example.model.cards.interfaces.CardAction;

public class MonopolyCard implements CardAction {

    @Override
    public void execute(GameModel model, int playerID) {
        // allows the player to choose a resource type
        // forces all other players to give up all of that resource type
    }
}
