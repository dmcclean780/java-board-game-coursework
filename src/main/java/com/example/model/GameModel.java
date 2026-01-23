package com.example.model;

import java.util.ArrayList;

public class GameModel {
    private ArrayList<Player> players;
    private Tiles tiles;
    private Ports ports;
    private Roads roads;
    private Settlements settlements;
    private Dice dice;
    private BankCards bankCards;
    private ClimateTracker climateTracker;

    public GameModel() {
        this.players = new ArrayList<>();
        this.tiles = new Tiles();
        this.ports = new Ports();
        this.roads = new Roads();
        this.settlements = new Settlements();
        this.dice = new Dice();
        this.bankCards = new BankCards();
        this.climateTracker = new ClimateTracker();
    }

    public void initializePlayers(ArrayList<String> playerNames) {
        for (String name : playerNames) {
            players.add(new Player(name));
        }
    }

    public int getNumberOfTiles() {
        return tiles.getTiles().length;
    }

    public int getNumberOfVertices() {
        int[][] vertexPerTile = AdjacencyMaps.TileVertices;
        int vertixCount = 0;
        for (int[] vertices : vertexPerTile) {
            vertixCount += vertices.length;
        }
        return vertixCount;
    }

    public Tile[] getTiles() {
        return tiles.getTiles();
    }

    public Settlement[] getSettlements() {
        return settlements.getAllSettlements();
    }

    public String getSettlmentType(int index) {
        return settlements.getAllSettlements()[index].getSettlementType();
    }

    public int getSettlmentOwner(int index) {
        return settlements.getAllSettlements()[index].getPlayerID();
    }

    public boolean settlementValid(int vertex, int playerIndex) {
        int playerID = players.get(playerIndex).getId();
        boolean settlementDistanceValid = !settlements.nearbySettlement(vertex); // Note: settlement distance rule is valid when *NOT* a nearby settlement
        boolean linkedByRoad = roads.isVertexConnectedByPlayer(vertex, playerID);
        boolean unowned = getSettlmentOwner(vertex) == Settlements.UNOWNED_SETTLEMENT_ID;
        return settlementDistanceValid && linkedByRoad && unowned;
    }

    public boolean cityValid(int vertex, int playerIndex) {
        int playerID = players.get(playerIndex).getId();
        boolean isOwner = getSettlmentOwner(vertex) == playerID;
        boolean notAlreadyCity = !settlements.getAllSettlements()[vertex].isCity();
        return isOwner && notAlreadyCity;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean buildSettlement(int vertex, int playerID) {
        // REMOVE RESOUCES FROM PLAYER HERE
        boolean success = settlements.buildSettlement(vertex, playerID);
        if (success) {
            increaseClimateAndDistributeDisasterCards();
        }
        return success;
    }

    public boolean buildCity(int vertex, int playerID) {
        // REMOVE RESOUCES FROM PLAYER HERE
        return settlements.upgradeSettlement(vertex, playerID);
    }

    /*
    also need to call this function:
            when settlements get resources
                -is resource distribution being done yet??
                -if not i can do it
            when certain devcards are played
                (trading frenzy, highway madness, monopoly)
                -need to check which devcard before calling
                -are devcards distribution done?
                -again i can do this if not
    */
    //also where should tile restoration be implemented
    public void increaseClimateAndDistributeDisasterCards() {
        climateTracker.increaseClimate();
        
        if (climateTracker.shouldGiveDisasterCard()) {
            int numCards = climateTracker.disasterCardNum();
            for (int i = 0; i < numCards; i++) {
                String disasterCard = bankCards.giveDisasterCard();
                if (!disasterCard.isEmpty()) {
                    //give disaster card
                    //idk how yet
                }
                //do nothing if no cards are left??
            }
        }
    }
}
