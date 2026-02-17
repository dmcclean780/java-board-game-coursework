package com.example.viewmodel;

public enum TurnState {
    DICE_ROLL("hint.turn.roll_dice"),
    TRADE("hint.turn.trade"),
    BUILD("hint.turn.build"),
    BUILD_SETTLEMENT("hint.turn.build"),
    BUILD_ROAD("hint.turn.build"),
    BUILD_CITY("hint.turn.build"),
    MOVE_ROBBER_STATE("hint.turn.build");

    private final String hintKey;

    TurnState(String hintKey) {
        this.hintKey = hintKey;
    }

    public String getHintKey() {
        return hintKey;
    }
}
