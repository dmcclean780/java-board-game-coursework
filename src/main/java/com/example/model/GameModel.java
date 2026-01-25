package com.example.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.example.model.config.ResourceConfig;
import com.example.model.config.registry.ResourceRegistry;
import com.example.model.config.service.ConfigService;
import com.example.model.trading.*;

public class GameModel {
    private ArrayList<Player> players;
    private Tiles tiles;
    private Ports ports;
    private Roads roads;
    private Settlements settlements;
    private Dice dice;
    private BankCards bankCards;

    public GameModel() {
        this.players = new ArrayList<>();
        this.tiles = new Tiles();
        this.ports = new Ports();
        this.roads = new Roads();
        this.settlements = new Settlements();
        this.dice = new Dice();
        this.bankCards = new BankCards();
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

    public Player getPlayer(int playerID) {
        for (Player player : players) {
            if (player.getId() == playerID) {
                return player;
            }
        }
        return null;
    }

    public boolean buildSettlement(int vertex, int playerID) {
        // REMOVE RESOUCES FROM PLAYER HERE
        return settlements.buildSettlement(vertex, playerID);
    }

    public boolean buildCity(int vertex, int playerID) {
        // REMOVE RESOUCES FROM PLAYER HERE
        return settlements.upgradeSettlement(vertex, playerID);
    }


    public boolean validTrade(TradePlayer trade) {
        if (trade.playerAId() == trade.playerBId()) {
            return false;
        }
        Player playerA = getPlayer(trade.playerAId());
        Player playerB = getPlayer(trade.playerBId());
        
        int playerAResourceCount = playerA.getResourceCount(trade.resourceAGive());
        if (playerAResourceCount < trade.amountA()) {
            return false;
        }

        int playerBResourceCount = playerB.getResourceCount(trade.resourceBGive());
        if (playerBResourceCount < trade.amountB()) {
            return false;
        }

        return true;
    }

    public boolean validTrade(TradeBank trade) {

        int bankResourceCount = bankCards.getResourceCount(trade.recieveResource());
        if (bankResourceCount <= 0) {
            return false;
        }

        Player player = getPlayer(trade.playerId());
       
        int playerResourceCount = player.getResourceCount(trade.giveResource());
        if (playerResourceCount < TradeBank.TRADE_RATE) {
            return false;
        }
        
        return true;
    }

    public boolean validTrade(TradePort trade) {
        Player player = getPlayer(trade.playerId());

        int playerResourceCount = player.getResourceCount(ResourceRegistry.getInstance().get(trade.port().resourceID)); // gets the resource being given to the port;                                    
        if (playerResourceCount < trade.port().giveQuantity) {                                                          // the number of resources the player has of that type
            return false;
        }

        int bankResourceCount = bankCards.getResourceCount(trade.resource());
        if (bankResourceCount < trade.port().receiveQuantity) {
            return false;
        }


        return true;
    }

    public boolean executeTrade(TradePlayer trade) {
        if (!validTrade(trade)) {
            return false;
        }

        Player playerA = getPlayer(trade.playerAId());
        Player playerB = getPlayer(trade.playerBId());
        playerA.changeResourceCount(trade.resourceAGive(), -trade.amountA());
        playerB.changeResourceCount(trade.resourceBGive(), -trade.amountB());

        playerA.changeResourceCount(trade.resourceBGive(), +trade.amountB());
        playerB.changeResourceCount(trade.resourceAGive(), +trade.amountA());

        return true;
    }


    public boolean executeTrade(TradeBank trade) {
        if (!validTrade(trade)) {
            return false;
        }
        Player player = getPlayer(trade.playerId());

        bankCards.giveResourceCard(trade.recieveResource(), 1);
        player.changeResourceCount(trade.giveResource(), -TradeBank.TRADE_RATE);

        bankCards.returnResourceCard(trade.giveResource(), TradeBank.TRADE_RATE);
        player.changeResourceCount(trade.recieveResource(), +1);

        return true;
    }

    public boolean executeTrade(TradePort trade) {
        if (!validTrade(trade)) {
            return false;
        }
        Player player = getPlayer(trade.playerId());

        bankCards.giveResourceCard(trade.resource(), trade.port().receiveQuantity);
        player.changeResourceCount(ResourceRegistry.getInstance().get(trade.port().resourceID), -trade.port().giveQuantity);

        bankCards.returnResourceCard(ResourceRegistry.getInstance().get(trade.port().resourceID), trade.port().giveQuantity);
        player.changeResourceCount(trade.resource(), +trade.port().receiveQuantity);

        return true;
    }

    /* 

    //method to give players resources based on the dice roll
    public void GiveResourcesToPlayers(int diceroll){
        //Find tiles on the dice number
        ArrayList<Tile> foundTiles = tiles.GetTilesFromDiceroll(diceroll);
        int[][] foundVertices = new int[foundTiles.size()][6];
        //this for loop gets all the vertices on the tiles we found
        for (int i = 0; i < foundTiles.size(); i++){
            for (int j = 0; j < 6; j++){
                foundVertices[i][j] = foundTiles.get(i).getAdjVertices()[j];
            }
        }

        //for each tile found:
        //  return all settlements on each tile number

        //for each tile found with that number, get resources for those players
        ArrayList<Settlement> currentSettlements = new ArrayList<Settlement>();
        //2D array represents resource's to be taken, and number of players taking from it
        //0=wood, 1=brick, 2=sheep, 3=wheat, 4=rock
        int[][] resourcesTakenFromBank = new int[5][2];
        //hashmap stores playerID (value 0 in int[]), resource (key) and amount they're to recieve (value 1 in int[])
        HashMap<String, int[]> playersAndTheirNewResources = new HashMap<>();
        //for each tile with that diceroll
        for (int i = 0; i < foundTiles.size(); i++){
            currentSettlements = settlements.GetSettlementsOnTile(foundVertices[i]);
            int tileResource = 0;
            switch (foundTiles.get(i).getTileID()){
                case "tile.forest": tileResource = 0; break;
                case "tile.hills": tileResource = 1; break;
                case "tile.pasture": tileResource = 2; break;
                case "tile.fields": tileResource = 3; break;
                case "tile.mountains": tileResource = 4; break;
            }

            ArrayList<Integer> playerIDs = new ArrayList<Integer>();
            int resourcesToBeCollected = 0;
            //for each settlement on that tile
            for (int j = 0; j < currentSettlements.size(); j++){

                //if this player has no other settlements on that tile in the list yet, add to the unique list of players collecting from that tile
                boolean inPlayerArrayList = false;
                for (int k = 0; k < playerIDs.size(); k++) { 
                    if (currentSettlements.get(j).getPlayerID() == playerIDs.get(k)){inPlayerArrayList = true;}
                }
                if (!inPlayerArrayList) {playerIDs.add(currentSettlements.get(j).getPlayerID());}

                //add one resource to be collected, plus another if settlement is city
                resourcesToBeCollected++;
                if (currentSettlements.get(j).isCity()){resourcesToBeCollected++;}
            }

            //update array of resources to take from bank and players to distribute between
            resourcesTakenFromBank[tileResource][0] += resourcesToBeCollected;
            resourcesTakenFromBank[tileResource][1] += playerIDs.size();
            //NOTE: When a diceroll results in two tiles of the same resource being activated where only one player
            //      has settlement(s) on both tiles, the code will not allow them to collect any resources.
            //      This is unintended, but fixing this is complicated and beyond the scope of this project
        }
        

        //  find how many resources of each type are to given out (and if it exceeds the bank)
        Collection<ResourceConfig> allResources = ConfigService.getAllResources();
        int i = 0;
        for (ResourceConfig resource : allResources){
            
            //if each resource to be taken outnumbers resources left in bank, no one receives anything
            if (resourcesTakenFromBank[i][0] > bankCards.getResourceCount(resource)){
                //BUT only if multiple players are involved, otherwise the one player gets all remaining resource
                if (resourcesTakenFromBank[i][1] == 1){
                    //subtracts resources from bank
                    int remainingResourcesInBank = bankCards.getResourceCount(resource);
                    bankCards.giveResourceCard(resource, remainingResourcesInBank);
                    //gives resources to players

                }
            }
            //bank not exceeded
            else{
                //subtracts resources from bank
                bankCards.giveResourceCard(resource, resourcesTakenFromBank[i][0]);
                //gives resources to players

            }

            i++;
        }

        //  if bank exceeded, give/don't give resources based on number of players involved
        //  give resources to players and decrement bank
    }*/


    //method to give players resources based on the dice roll
    public void GiveResourcesToPlayers(int diceroll){
        for (Tile tile : tiles.GetTilesFromDiceroll(diceroll)){
            //get resource of rolled tile
            ResourceConfig resource = tile.getResourceFromTileID();

            //get all vertices on that tile
            for (int vertex : tile.getAdjVertices()){

                //check if there's a settlement on that vertex
                Settlement currentSettlement = settlements.GetSettlementFromVertex(vertex);
                if (currentSettlement == null){
                    continue; //no settlement found, skip the rest of this method
                }

                //find the player who owns the settlement
                Player player = getPlayerFromID(currentSettlement.getPlayerID());
                if (player == null) {throw new IllegalStateException("Player not found for settlement");}

                int production = 1;
                if (currentSettlement.isCity()) { production++; }

                //for loop accounts for cities giving two resources, whilst still ensuring
                //that when only one resource is left, a city will still produce one
                for (int i = 0; i < production; i++){
                    //check if there is a free resource left in the bank
                    if (bankCards.giveResourceCard(resource, 1)){
                        player.changeResourceCount(resource, 1);
                    }
                    //bank empty, stop giving out resources
                    else{break;}
                }
                
            }
        }
    }

    public Player getPlayerFromID(int ID){
        for (Player player : players){
            if (player.getId() == ID){
                //found player
                return player;
            }
        }
        //failed
        return null;
    }
}
