package com.example.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


import com.example.model.config.PlayerInfrastructureConfig;

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

    public boolean settlementValid(int vertex, int playerID) {
        boolean settlementDistanceValid = !settlements.nearbySettlement(vertex); // Note: settlement distance rule is valid when *NOT* a nearby settlement
        boolean linkedByRoad = roads.isVertexConnectedByPlayer(vertex, playerID);
        boolean unowned = getSettlmentOwner(vertex) == Settlements.UNOWNED_SETTLEMENT_ID;
        return settlementDistanceValid && linkedByRoad && unowned || true;
    }

    public boolean cityValid(int vertex, int playerID) {
        boolean isOwner = getSettlmentOwner(vertex) == playerID;
        boolean notAlreadyCity = !settlements.getAllSettlements()[vertex].isCity();
        return isOwner && notAlreadyCity;
    }

    public boolean roadValid(int edgeIndex, int playerID) {
        //boolean connectedToSettlement = settlements.isEdgeConnectedToPlayerSettlement(edgeIndex, playerID); TODO
        //boolean connectedToRoad = roads.isEdgeConnectedByPlayer(edgeIndex, playerID); TODO
        boolean unowned = roads.isRoadOwned(edgeIndex) == false;
        return /*(connectedToSettlement || connectedToRoad) &&*/ unowned || true;
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

    public int nextPlayer(int currentPlayerId){
        int currentIndex = -1;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getId() == currentPlayerId) {
                currentIndex = i;
                break;
            }
        }
        int nextIndex = (currentIndex + 1) % players.size();
        return players.get(nextIndex).getId();
    }

    public boolean buildSettlement(int vertex, int playerID) {
        boolean success_build = settlements.buildSettlement(vertex, playerID);
        String structureID = settlements.getAllSettlements()[vertex].getSettlementType();
        boolean success_resources = getPlayer(playerID).deductStructureResources(structureID);
        if (success_resources && success_build) {
            increaseClimateAndDistributeDisasterCards();
        }
        return success_resources && success_build;
    }

    public boolean playerHasSettlementResources(int playerID) {
        Player player = getPlayer(playerID);
        return player.hasEnoughResourcesForStructure("player_infrastructure.settlement");
    }

    public boolean buildCity(int vertex, int playerID) {
        boolean success_upgrade = settlements.upgradeSettlement(vertex, playerID);
        String structureID = settlements.getAllSettlements()[vertex].getSettlementType();
        boolean success_resources = getPlayer(playerID).deductStructureResources(structureID);
        return success_resources && success_upgrade;
    }

    public boolean playerHasCityResources(int playerID) {
        Player player = getPlayer(playerID);
        return player.hasEnoughResourcesForStructure("player_infrastructure.city");
    }

    public boolean buildRoad(int edgeIndex, int playerID) {
        boolean success_build = roads.buildRoad(edgeIndex, playerID);
        String structureID = roads.getAllRoads()[edgeIndex].getRoadType();
        boolean success_resources = getPlayer(playerID).deductStructureResources(structureID);
        return success_resources && success_build;
    }

    public boolean playerHasRoadResources(int playerID) {
        Player player = getPlayer(playerID);
        return player.hasEnoughResourcesForStructure("player_infrastructure.road");
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
                Player player = getPlayer(currentSettlement.getPlayerID());
                if (player == null) {throw new IllegalStateException("Player not found for settlement");}

                int production = 1;
                if (currentSettlement.isCity()) { production++; }

                //for loop accounts for cities giving two resources, whilst still ensuring
                //that when only one resource is left, a city will still produce one
                for (int i = 0; i < production; i++){
                    //check if there is a free resource left in the bank
                    if (bankCards.giveResourceCard(resource, 1)){
                        player.changeResourceCount(resource, 1);
                        // Only settlements (not cities) cause climate to increase / disaster cards to be considered.
                        if (!currentSettlement.isCity()) {
                            increaseClimateAndDistributeDisasterCards();
                        }
                    }
                    //bank empty, stop giving out resources
                    else{break;}
                }
                
            }
        }
    }

    public Road[] getRoads() {
        return roads.getAllRoads();
    }

    //need to do front end stuff to chose tile to destroy
    //asks for same resource as tile atm - need to fix
    public boolean tileRestore(int tileIndex, int playerId) {
        Tile[] allTiles = tiles.getTiles();
        if (tileIndex < 0 || tileIndex >= allTiles.length) {
            return false;
        }
        Tile tile = allTiles[tileIndex];
        ResourceConfig resource = tile.getResourceFromTileID();
        if (resource == null) {
            return false; // desert or no-resource tile cannot be restored
        }
        if (!tile.getIsDestroyed()) {
            return false; // nothing to restore
        }

        Player player = getPlayer(playerId);
        if (player == null) {
            return false;
        }

        // derive the infrastructure id for this tile
        String structureId = tile.getTileID().replace("tile.", "player_infrastructure.") + "_tile";
        PlayerInfrastructureConfig cfg = ConfigService.getInfrastructure(structureId);
        if (cfg == null || cfg.constructionCosts.isEmpty()) {
            return false; // no configured cost for this tile
        }

        // check player has the required resources
        if (!player.hasEnoughResourcesForStructure(structureId)) {
            return false;
        }

        // deduct configured resources
        boolean deducted = player.deductStructureResources(structureId);
        if (!deducted) {
            return false;
        }

        // attempt restore; if it fails, refund the deducted resources
        boolean restored = tiles.restoreTile(tileIndex);
        if (!restored) {
            // refund: add back each configured resource and also return them to the bank
            for (String resourceID : cfg.constructionCosts.keySet()) {
                ResourceConfig r = ConfigService.getResource(resourceID);
                int amount = cfg.constructionCosts.get(resourceID);
                player.changeResourceCount(r, amount);
                bankCards.returnResourceCard(r, amount);
            }
            return false;
        }

        // on success, return the deducted resources to the bank
        for (String resourceID : cfg.constructionCosts.keySet()) {
            ResourceConfig r = ConfigService.getResource(resourceID);
            int amount = cfg.constructionCosts.get(resourceID);
            bankCards.returnResourceCard(r, amount);
        }

        return true;
    }

    /*
    also need to call this function:
            when settlements get resources
            when certain devcards are played
                (trading frenzy, highway madness, monopoly)
                -need to check which devcard before calling
    */
    //also where should tile restoration be implemented
    //for restore tile i need unique id for each tile to know which one to restore
    public void increaseClimateAndDistributeDisasterCards() {
        climateTracker.increaseClimate();
        
        if (climateTracker.shouldGiveDisasterCard()) {
            int numCards = climateTracker.disasterCardNum();
            for (int i = 0; i < numCards; i++) {
                String disasterCard = bankCards.giveDisasterCard();
                if (!disasterCard.isEmpty()) {
                    //give disaster card
                    //destroy tile
                    tiles.destroyTile(disasterCard);
                }
                //do nothing if no cards are left??
            }
        }
    }

    public boolean buyDevelopmentCard(int playerId) {
        Player player = getPlayer(playerId);
        if (player == null) return false;

        // check & deduct cost
        if (!player.hasEnoughResourcesForStructure("player_infrastructure.dev_card")) {
            return false;
        }
        boolean deducted = player.deductStructureResources("player_infrastructure.dev_card");
        if (!deducted) return false;

        // attempt to draw from bank
        String devCardId = bankCards.giveDevelopmentCard();
        if (devCardId == null || devCardId.isEmpty()) {
            // refund resources
            PlayerInfrastructureConfig cfg = ConfigService.getInfrastructure("player_infrastructure.dev_card");
            for (String resourceID : cfg.constructionCosts.keySet()) {
                ResourceConfig r = ConfigService.getResource(resourceID);
                int amount = cfg.constructionCosts.get(resourceID);
                player.changeResourceCount(r, amount);
            }
            return false;
        }

        // deliver the card (handles victory-point semantics)
        handleReceivedDevelopmentCard(playerId, devCardId);
        return true;
    }

    private void handleReceivedDevelopmentCard(int playerId, String devCardId) {
        Player player = getPlayer(playerId);
        if (player == null) return;

        com.example.model.config.DevCardConfig cfg = ConfigService.getDevCard(devCardId);
        if (cfg == null) {
            // unknown card: treat as no-op
            return;
        }

        String action = cfg.actionType == null ? "" : cfg.actionType;
        if ("VICTORY_POINT".equals(action)) {
            // award invisible VP immediately and do NOT add the card to hand
            player.addInvisibleVictoryPoints(1);
            return;
        }

        // other dev-cards are added to the player's hand and may be played
        player.addCard(devCardId);
    }

    public boolean playDevCard(int playerId, String devCardId) {
        Player player = getPlayer(playerId);
        if (player == null) return false;

        if (!player.hasCard(devCardId)) return false;

        com.example.model.config.DevCardConfig cfg = ConfigService.getDevCard(devCardId);
        if (cfg == null) return false;

        String action = cfg.actionType == null ? "" : cfg.actionType;
        if ("VICTORY_POINT".equals(action)) {
            // cannot be played
            return false;
        }

        // remove the card from the player's hand (played)
        boolean removed = player.removeCard(devCardId);
        if (!removed) return false;

        // dispatch to the appropriate effect handler
        switch (action) {
            case "ECO_CONFERENCE" -> applyEcoConference(playerId);
            case "HIGHWAY_MADNESS" -> applyHighwayMadness(playerId);
            case "TRADING_FRENZY" -> applyTradingFrenzy(playerId);
            case "MONOPOLY" -> applyMonopoly(playerId);
            default -> {
                // unknown action: no-op for now
            }
        }

        return true;
    }

    public int getPlayerVictoryPoints(int playerId) {
        Player p = getPlayer(playerId);
        int points = p.getVictoryPoints(playerId);
        if (p != null) points += p.getInvisibleVictoryPoints();
        return points;
    }

    public void applyEcoConference(int playerId) {
        // TODO: implement ECO_CONFERENCE effect
    }

    public void applyHighwayMadness(int playerId) {
        // TODO: implement HIGHWAY_MADNESS effect
        //also increase climate tracker
    }

    public void applyTradingFrenzy(int playerId) {
        // TODO: implement TRADING_FRENZY effect
        //also increase climate tracker
    }

    public void applyMonopoly(int playerId) {
        // TODO: implement MONOPOLY effect
        //also increase climate tracker
    }

    // TESTING METHODS
    public void giveSettlementResources(int playerID) {
        Player player = getPlayer(playerID);
        PlayerInfrastructureConfig config = ConfigService.getInfrastructure("player_infrastructure.settlement");
        for (String resourceID : config.constructionCosts.keySet()) {
            ResourceConfig resourceConfig = ConfigService.getResource(resourceID);
            int amount = config.constructionCosts.get(resourceID);
            player.changeResourceCount(resourceConfig, amount);
        }
    }

    // TESTING METHOD
    public void giveCityResources(int playerID) {
        Player player = getPlayer(playerID);
        PlayerInfrastructureConfig config = ConfigService.getInfrastructure("player_infrastructure.city");
        for (String resourceID : config.constructionCosts.keySet()) {
            ResourceConfig resourceConfig = ConfigService.getResource(resourceID);
            int amount = config.constructionCosts.get(resourceID);
            player.changeResourceCount(resourceConfig, amount);
        }
    }

    // TESTING METHOD
    public void giveRoadResources(int playerID) {
        Player player = getPlayer(playerID);
        PlayerInfrastructureConfig config = ConfigService.getInfrastructure("player_infrastructure.road");
        for (String resourceID : config.constructionCosts.keySet()) {
            ResourceConfig resourceConfig = ConfigService.getResource(resourceID);
            int amount = config.constructionCosts.get(resourceID);
            player.changeResourceCount(resourceConfig, amount);
        }
    }

}

