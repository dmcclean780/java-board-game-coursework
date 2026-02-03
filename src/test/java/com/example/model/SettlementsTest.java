package com.example.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import com.example.model.config.ConfigManager;

public class SettlementsTest {

    private Settlements settlements;

    @BeforeAll
    public static void setUpAll(){
        try {ConfigManager.loadAll();} catch (Exception e) { } // needed to load ResourceRegistry if not loaded, throws if already loaded    }
    }
    
    @BeforeEach
    public void setUp(){
        settlements = new Settlements();
    }

    //Settlements()

    @Test
    public void testConstructor_initialState(){
        Settlements s = new Settlements();

        assertEquals(Settlements.NUMBER_OF_VERTICES, s.getAllSettlements().length); // test that all vertices have unowned settlements associated

        for (int i = 0; i < Settlements.NUMBER_OF_VERTICES; i++) {
            assertEquals(Settlements.UNOWNED_SETTLEMENT_ID, s.ownedByPlayer(i));
        }

        assertEquals(0, s.getAllOwnedSettlements().length);

    }

    //buildSettlement()

    @Test
    public void testBuildSettlement_valid(){
        boolean result = settlements.buildSettlement(5, 1);
        assertTrue(result);
        assertEquals(1, settlements.ownedByPlayer(5));
    }

    @Test
    public void testBuildSettlement_invalidVertex(){
        assertFalse(settlements.buildSettlement(-1, 1));
        assertFalse(settlements.buildSettlement(100, 1));
    }

    @Test
    public void testBuildSettlement_alreadyOwned() {
        settlements.buildSettlement(10, 1);
        boolean result = settlements.buildSettlement(10, 2);
        assertFalse(result);
        assertEquals(1, settlements.ownedByPlayer(10));

    }

    //ownedByPlayer()

    @Test
    void testOwnedByPlayer_unowned() {
        assertEquals(Settlements.UNOWNED_SETTLEMENT_ID, settlements.ownedByPlayer(3));
    }

    @Test
    void testOwnedByPlayer_invalidVertexThrows() {
        assertThrows(IndexOutOfBoundsException.class, () -> settlements.ownedByPlayer(-5));
        assertThrows(IndexOutOfBoundsException.class, () -> settlements.ownedByPlayer(200));
    }

    //upgradeSettlement()

    @Test
    void testUpgradeSettlement_success() {
        settlements.buildSettlement(7, 2);
        boolean result = settlements.upgradeSettlement(7, 2);
        assertTrue(result);
    }

    @Test
    void testUpgradeSettlement_wrongPlayer() {
        settlements.buildSettlement(8, 1);
        assertFalse(settlements.upgradeSettlement(8, 2));
    }

    @Test
    void testUpgradeSettlement_invalidVertex() {
        assertFalse(settlements.upgradeSettlement(-1, 1));
        assertFalse(settlements.upgradeSettlement(100, 1));
    }

    //getAllSettlements()
    void testGetAllSettlements() {
        Settlement[] allSettlements = settlements.getAllSettlements();
        assertEquals(Settlements.NUMBER_OF_VERTICES, allSettlements.length);
        for (Settlement s : allSettlements) {
            assertNotNull(s);
            assertEquals(Settlements.UNOWNED_SETTLEMENT_ID, s.getPlayerID());
        }
    }

    //getAllOwnedSettlements()

    @Test
    void testGetAllOwnedSettlements() {
        settlements.buildSettlement(1, 1);
        settlements.buildSettlement(2, 1);
        settlements.buildSettlement(3, 2);

        Settlement[] owned = settlements.getAllOwnedSettlements();

        assertEquals(3, owned.length);
        assertTrue(owned[0].getPlayerID() != Settlements.UNOWNED_SETTLEMENT_ID);
    }

    @Test
    void testGetAllOwnedSettlements_noneOwned() {
        Settlement[] owned = settlements.getAllOwnedSettlements();
        assertEquals(0, owned.length);
    }

    //getVictoryPoints()

    @Test
    void testGetVictoryPoints() {
        settlements.buildSettlement(4, 1);
        settlements.buildSettlement(5, 1);

        int points = settlements.getVictoryPoints(1);

        assertTrue(points >= 2);
    }

    @Test
    void testGetVictoryPointsNoSettlements() {
        assertEquals(0, settlements.getVictoryPoints(99));
    }

    //isValidVertex()

    @Test
    void testIsValidVertex() {
        assertTrue(Settlements.isValidVertex(0));
        assertTrue(Settlements.isValidVertex(Settlements.NUMBER_OF_VERTICES - 1));
        assertFalse(Settlements.isValidVertex(-1));
        assertFalse(Settlements.isValidVertex(100));
    }

    //nearbySettlement()

    @Test
    void testNearbySettlement() {
        settlements.buildSettlement(10, 1);
        settlements.buildSettlement(20, 2);

        // Checked to match expected by AdjacencyMaps.RoadConnections
        assertTrue(settlements.nearbySettlement(11)); // adjacent to 10
        assertTrue(settlements.nearbySettlement(9));  // adjacent to 10
        assertTrue(settlements.nearbySettlement(19)); // adjacent to 20
        assertTrue(settlements.nearbySettlement(21)); // adjacent to 20

        assertFalse(settlements.nearbySettlement(15)); // not near any settlement
    }

}
