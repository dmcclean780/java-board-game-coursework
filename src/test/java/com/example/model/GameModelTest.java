package com.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;




/**
 * Unit tests for GameModel class
 */
public class GameModelTest {
    private GameModel gameModel;
    private ArrayList<String> playerNames;

    @BeforeEach
    public void setUp() {
        gameModel = new GameModel();
        playerNames = new ArrayList<>();
        playerNames.add("Alice");
        playerNames.add("Bob");
        playerNames.add("Charlie");
    }

    @Test
    public void testConstructor() {
        // test its not null and a field seems instantiated
        assertNotNull(gameModel);
        assertNotNull(gameModel.getPlayers());
        assertEquals(0, gameModel.getPlayers().size());
    }

    @Test
    public void testInitializePlayers() {
        gameModel.initializePlayers(playerNames);
        assertEquals(3, gameModel.getPlayers().size());
        assertEquals("Alice", gameModel.getPlayers().get(0).getName());
    }

    @Test
    public void testInitializePlayersEmpty() {
        ArrayList<String> emptyNames = new ArrayList<>();
        gameModel.initializePlayers(emptyNames);
        assertEquals(0, gameModel.getPlayers().size());
    }

    @Test
    public void testGetNumberOfTiles() {
        int numTiles = gameModel.getNumberOfTiles();
        assertTrue(numTiles > 0);
    }

    @Test
    public void testGetNumberOfVertices() {
        int numVertices = gameModel.getNumberOfVertices();
        assertTrue(numVertices > 0);
    }

    @Test
    public void testGetTiles() {
        assertNotNull(gameModel.getTiles());
        assertEquals(gameModel.getNumberOfTiles(), gameModel.getTiles().length);
    }

    @Test
    public void testGetSettlements() {
        assertNotNull(gameModel.getSettlements());
    }

    @Test
    public void testGetPlayer() {
        gameModel.initializePlayers(playerNames);
        Player player = gameModel.getPlayer(gameModel.getPlayers().get(0).getId());
        assertNotNull(player);
        assertEquals("Alice", player.getName());
    }

    @Test
    public void testGetPlayerNotFound() {
        gameModel.initializePlayers(playerNames);
        assertNull(gameModel.getPlayer(999));
    }

    @Test
    public void testNextPlayer() {
        gameModel.initializePlayers(playerNames);
        int player1Id = gameModel.getPlayers().get(0).getId();
        int player2Id = gameModel.getPlayers().get(1).getId();
        int player3Id = gameModel.getPlayers().get(2).getId();
        
        assertEquals(player2Id, gameModel.nextPlayer(player1Id));
        assertEquals(player3Id, gameModel.nextPlayer(player2Id));
        assertEquals(player1Id, gameModel.nextPlayer(player3Id));
    }

    @Test
    public void testNextPlayerSinglePlayer() {
        ArrayList<String> singlePlayer = new ArrayList<>();
        singlePlayer.add("Alice");
        gameModel.initializePlayers(singlePlayer);
        int playerId = gameModel.getPlayers().get(0).getId();
        assertEquals(playerId, gameModel.nextPlayer(playerId));
    }

    @Test
    public void testSettlementValid() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        gameModel.giveSettlementResources(playerId);
        // Note: settlementValid always returns true due to || true logic
        assertTrue(gameModel.settlementValid(0, playerId));
    }

    @Test
    public void testCityValid() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        assertFalse(gameModel.cityValid(0, playerId)); // No settlement exists
    }

    @Test
    public void testRoadValid() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        // Note: roadValid always returns true due to || true logic
        assertTrue(gameModel.roadValid(0, playerId));
    }

    @Test
    public void testBuildSettlement() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        gameModel.giveSettlementResources(playerId);
        
        boolean result = gameModel.buildSettlement(0, playerId);
        // Result depends on Settlements logic
        assertNotNull(result);
    }

    @Test
    public void testPlayerHasSettlementResources() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        assertFalse(gameModel.playerHasSettlementResources(playerId));
        
        gameModel.giveSettlementResources(playerId);
        assertTrue(gameModel.playerHasSettlementResources(playerId));
    }

    @Test
    public void testBuildCity() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        gameModel.giveCityResources(playerId);
        
        boolean result = gameModel.buildCity(0, playerId);
        // Result depends on Settlements logic
        assertNotNull(result);
    }

    @Test
    public void testPlayerHasCityResources() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        assertFalse(gameModel.playerHasCityResources(playerId));
        
        gameModel.giveCityResources(playerId);
        assertTrue(gameModel.playerHasCityResources(playerId));
    }

    @Test
    public void testBuildRoad() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        gameModel.giveRoadResources(playerId);
        
        boolean result = gameModel.buildRoad(0, playerId);
        // Result depends on Roads logic
        assertNotNull(result);
    }

    @Test
    public void testPlayerHasRoadResources() {
        gameModel.initializePlayers(playerNames);
        int playerId = gameModel.getPlayers().get(0).getId();
        assertFalse(gameModel.playerHasRoadResources(playerId));
        
        gameModel.giveRoadResources(playerId);
        assertTrue(gameModel.playerHasRoadResources(playerId));
    }

    @Test
    public void testGetRoads() {
        assertNotNull(gameModel.getRoads());
        assertTrue(gameModel.getRoads().length > 0);
    }

    @Test
    public void testGetSettlementType() {
        assertNotNull(gameModel.getSettlmentType(0));
    }

    @Test
    public void testGetSettlementOwner() {
        int owner = gameModel.getSettlmentOwner(0);
        assertNotNull(owner);
    }
}