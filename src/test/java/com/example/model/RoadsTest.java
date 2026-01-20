package com.example.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

/**
 * Unit tests for Roads class
 * @author 40452739
 */
public class RoadsTest {

    // Generated tests using Copilot, tweaked and fixed where necessary (copilot could not understand minimumLongestRoadLength logic)

    private Roads roads;

    @BeforeEach
    public void setUp() {
        roads = new Roads();
    }

    @Test
    public void testConstructor() {
        assertNotNull(roads);
        assertEquals(Roads.NUMBER_OF_ROADS, AdjacencyMaps.RoadConnections.length);
    }

    @Test
    public void testBuildRoadByIndex() {
        assertTrue(roads.buildRoad(0, 1));
        assertEquals(1, roads.ownedByPlayer(0));
        assertFalse(roads.buildRoad(0, 2)); // already owned
    }

    @Test
    public void testBuildRoadByVertices() {
        assertTrue(roads.buildRoad(0, 1, 2));
        assertEquals(2, roads.ownedByPlayer(0, 1));
    }

    @Test
    public void testBuildRoadInvalidPlayerID() {
        assertFalse(roads.buildRoad(0, 1, Roads.UNOWNED_ROAD_ID));
    }

    @Test
    public void testBuildRoadInvalidIndex() {
        assertFalse(roads.buildRoad(100, 1));
        assertFalse(roads.buildRoad(-1, 1));
    }

    @Test
    public void testOwnedByPlayerIndex() {
        roads.buildRoad(5, 1);
        assertEquals(1, roads.ownedByPlayer(5));
    }

    @Test
    public void testOwnedByPlayerVertices() {
        roads.buildRoad(0, 1, 3);
        assertEquals(3, roads.ownedByPlayer(0, 1));
    }

    @Test
    public void testIsVertexConnectedByPlayer() {
        roads.buildRoad(0, 1, 4);
        assertTrue(roads.isVertexConnectedByPlayer(0, 4));
        assertFalse(roads.isVertexConnectedByPlayer(0, 5));

        assertTrue(roads.isVertexConnectedByPlayer(1, 4));
        assertFalse(roads.isVertexConnectedByPlayer(0, 5));

        assertFalse(roads.isVertexConnectedByPlayer(2, 4));
    }   

    @Test
    public void testOwnedByPlayerInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> roads.ownedByPlayer(100));
    }

    @Test
    public void testIsRoadOwnedIndex() {
        assertFalse(roads.isRoadOwned(0));
        roads.buildRoad(0, 1);
        assertTrue(roads.isRoadOwned(0));
    }

    @Test
    public void testIsRoadOwnedVertices() {
        assertFalse(roads.isRoadOwned(0, 1));
        roads.buildRoad(0, 1, 1);
        assertTrue(roads.isRoadOwned(0, 1));
    }

    @Test
    public void testRemoveRoadByIndex() {
        roads.buildRoad(0, 1);
        assertTrue(roads.removeRoad(0));
        assertEquals(Roads.UNOWNED_ROAD_ID, roads.ownedByPlayer(0));
        assertFalse(roads.removeRoad(0)); // already unowned
    }

    @Test
    public void testRemoveRoadByVertices() {
        roads.buildRoad(0, 1, 1);
        assertTrue(roads.removeRoad(0, 1));
        assertFalse(roads.isRoadOwned(0, 1));
    }

    @Test
    public void testRemoveRoadInvalidIndex() {
        assertThrows(IndexOutOfBoundsException.class, () -> roads.removeRoad(100));
    }

    @Test
    public void testGetAllRoads() {
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 1);
        roads.buildRoad(5, 2);
        
        Road[] ownedRoads = roads.getAllRoads();
        assertEquals(3, ownedRoads.length);
    }

    @Test
    public void testGetAllRoadsEmpty() {
        Road[] ownedRoads = roads.getAllRoads();
        assertEquals(0, ownedRoads.length);
    }

    @Test
    public void testIsValidRoadIndex() {
        assertTrue(Roads.isValidRoadIndex(0));
        assertTrue(Roads.isValidRoadIndex(71));
        assertFalse(Roads.isValidRoadIndex(-1));
        assertFalse(Roads.isValidRoadIndex(72));
    }

    @Test
    public void testIsValidVertices() {
        assertTrue(Roads.isValidVertices(0, 1));
        assertTrue(Roads.isValidVertices(1, 0)); // order shouldn't matter
        assertFalse(Roads.isValidVertices(100, 200));
    }

    @Test
    public void testGetRoadIndex() {
        assertEquals(0, Roads.getRoadIndex(0, 1));
        assertEquals(0, Roads.getRoadIndex(1, 0)); // reverse order
        assertEquals(-1, Roads.getRoadIndex(100, 200));
    }

    @Test
    public void testLongestRoadOwnerSinglePlayer() {
        Roads.minimumLongestRoadLength = 0;
        int[] players = {1};
        assertEquals(Roads.UNOWNED_ROAD_ID, roads.longestRoadOwner(players));

        roads.buildRoad(0, 1);
        roads.buildRoad(1, 1);
        roads.buildRoad(2, 1);
        
        assertEquals(1, roads.longestRoadOwner(players));
    }

    @Test
    public void testLongestRoadOwnerMultiplePlayers() {
        
        Roads.minimumLongestRoadLength = 3;
        roads.buildRoad(0, 1, 1);
        roads.buildRoad(1, 2, 1);
        roads.buildRoad(2, 3, 1);
        roads.buildRoad(3, 4, 2);
        roads.buildRoad(6, 14, 2);
        roads.buildRoad(20, 31, 3);
        
        
        int[] players = {1, 2, 3};
        assertEquals(1, roads.longestRoadOwner(players));
    }

    @Test
    public void testLongestRoadOwnerNoMinimumLength() {
        
        Roads.minimumLongestRoadLength = 0;
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 2);
        
        int[] players = {1};
        assertEquals(1, roads.longestRoadOwner(players));
    }

    @Test
    public void testLongestRoadExists() {
        Roads.minimumLongestRoadLength = 3;
        int[] players = {1};
        
        assertFalse(roads.longestRoadExists(players));

        roads.buildRoad(0, 1);
        roads.buildRoad(1, 1);
        roads.buildRoad(2, 1);
        
        assertTrue(roads.longestRoadExists(players));
    }

    @Test
    public void testLongestRoadNotExists() {
        
        Roads.minimumLongestRoadLength = 3;
        roads.buildRoad(0, 1);
        
        int[] players = {1};
        assertFalse(roads.longestRoadExists(players));
    }

    @Test
    public void testGetLongestRoadLength() {
        Roads.minimumLongestRoadLength = 0;
        int[] players = {1};
        assertEquals(0, roads.getLongestRoadLength(players));
       
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 1);
        roads.buildRoad(2, 1);

    
        assertEquals(3, roads.getLongestRoadLength(players));
    }

    @Test
    public void testGetLongestRoadLengthMultiplePlayers() {
        Roads.minimumLongestRoadLength = 0;
        int[] players = {1, 2, 3};
        assertEquals(0, roads.getLongestRoadLength(players));
        
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 1);
        roads.buildRoad(2, 1);
        
        roads.buildRoad(5, 2);
        roads.buildRoad(6, 2);
        roads.buildRoad(61, 3);
        
        assertEquals(3, roads.getLongestRoadLength(players));
    }

    @Test
    public void testMultipleRoadsBuiltByDifferentPlayers() {
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 2, 2);
        
        assertEquals(1, roads.ownedByPlayer(0));
        assertEquals(2, roads.ownedByPlayer(1, 2));
    }

    @Test
    public void testBuildIDIncrement() throws NoSuchFieldException, IllegalAccessException {
        roads.buildRoad(0, 1);
        roads.buildRoad(1, 2);
        roads.buildRoad(2, 3);
        
        Field field = Roads.class.getDeclaredField("nextBuildID");
        field.setAccessible(true);
        assertEquals(4, field.getInt(roads));
    }
}
