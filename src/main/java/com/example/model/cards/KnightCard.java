package com.example.model.cards;
import com.example.model.cards.interfaces.CardAction;
import com.example.model.GameModel;

public class KnightCard implements CardAction {

    @Override
    public void execute(GameModel model, int playerID) {
        // moves the robber to a new position
        // allows the player to (optionally) attempt to steal a resource from an adjacent player
    }
}
