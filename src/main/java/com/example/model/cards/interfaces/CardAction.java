package com.example.model.cards.interfaces;
import com.example.model.GameModel;

public interface CardAction {
    public abstract void execute(GameModel model, int playerID);
}