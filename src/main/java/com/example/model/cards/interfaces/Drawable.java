package com.example.model.cards.interfaces;
import com.example.model.GameModel;

public interface Drawable {
    public abstract void onDraw(GameModel model, int playerID);
}