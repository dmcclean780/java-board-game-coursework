package com.example.model;

public class ClimateTracker {

    private int climateLevel;
    private int nextDisasterCard;

    public ClimateTracker() {
        this.climateLevel = 0;
        this.nextDisasterCard = 5;
    }

    public int getClimateLevel() { return climateLevel; }

    public void increaseClimate() {
        climateLevel++;
        if (climateLevel >= nextDisasterCard) {
            giveDisasterCard();
            nextDisasterCard += 5;
        }
    }

    public void giveDisasterCard() {
        int totalCards = 0;
        if (nextDisasterCard >= 5) {
            totalCards++;
        }
        if (nextDisasterCard >= 10) {
            totalCards++;
        }
        if (nextDisasterCard >= 15) {
            totalCards++;
        }
    }
    
}
