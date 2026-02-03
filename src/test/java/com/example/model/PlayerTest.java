package com.example.model;

import java.lang.reflect.Field;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.config.ConfigManager;
import com.example.model.config.registry.ResourceRegistry;





public class PlayerTest {

    @BeforeAll
    public static void setup() throws Exception {
        try {ConfigManager.loadAll();} catch (Exception e) { } // needed to load ResourceRegistry if not loaded, throws if already loaded
    }

    @BeforeEach
    public void resetNextId() throws Exception {
       
        Field nextIdField = Player.class.getDeclaredField("nextId");
        nextIdField.setAccessible(true);
        nextIdField.setInt(null, 1); // reset static nextId to 1 for deterministic ids
    }

    @Test
    public void constructor_initializesFieldsCorrectly() {
        Player p = new Player("Alice");
        assertEquals("Alice", p.getName());
        assertEquals(1, p.getId());

        // all resources initialized to 0
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wood")));
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.brick")));
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.sheep")));
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wheat")));
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.ore")));

        // total resources is 0 and no dev cards initially
        assertEquals(0, p.getTotalResources());
        assertEquals(0, p.numberOfCards());

        // structures remaining initial values; HAS TO MATCH MAX QUANTITIES IN player_infrastructure.json
        // assertEquals(15, p.getStructuresRemaining("player_infrastructure.road"));
        // assertEquals(5, p.getStructuresRemaining("player_infrastructure.settlement"));
        // assertEquals(4, p.getStructuresRemaining("player_infrastructure.city"));
        // assertEquals(25, p.getStructuresRemaining("player_infrastructure.dev_card"));
        // commented out as these values may change based on configuration
    }

    @Test
    public void setName_behaviour() {
        Player p = new Player("Bob");
        assertFalse(p.setName(null)); // should reject null
        assertEquals("Bob", p.getName());
        assertTrue(p.setName("Robert"));
        assertEquals("Robert", p.getName());
    }

    @Test
    public void resourceSetAndChange_behaviour() {
        Player p = new Player("ResTester");
        

        // valid set
        assertTrue(ResourceRegistry.getInstance().get("resource.wood") != null);
        assertTrue(p.setResourceCount(ResourceRegistry.getInstance().get("resource.wood"), 3));
        assertEquals(3, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wood")));
        assertEquals(3, p.getTotalResources());

        // invalid set (unknown type)
        assertFalse(p.setResourceCount(ResourceRegistry.getInstance().get("resource.gold"), 5));
        assertEquals(0, p.getResourceCount(ResourceRegistry.getInstance().get("resource.gold")));

        // change positive
        assertTrue(p.changeResourceCount(ResourceRegistry.getInstance().get("resource.wood"), 2));
        assertEquals(5, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wood")));

        // change negative but not below zero
        assertTrue(p.changeResourceCount(ResourceRegistry.getInstance().get("resource.wood"), -2));
        assertEquals(3, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wood")));
        // change negative below zero should fail
        assertFalse(p.changeResourceCount(ResourceRegistry.getInstance().get("resource.wood"), -10));
        assertEquals(3, p.getResourceCount(ResourceRegistry.getInstance().get("resource.wood")));

        // change unknown type should fail
        assertFalse(p.changeResourceCount(ResourceRegistry.getInstance().get("resource.gold"), 1));
    }

    @Test
    public void devCard_operations() {
        Player p = new Player("CardPlayer");

        // add null card rejected
        assertFalse(p.addCard(null));
        assertEquals(0, p.numberOfCards());

        // add cards
        assertTrue(p.addCard("dev.knight"));
        assertTrue(p.addCard("dev.victory_point"));
        assertTrue(p.addCard("dev.knight"));

        assertEquals(3, p.numberOfCards());
        assertEquals("dev.knight", p.getCard(0));
        assertEquals("dev.victory_point", p.getCard(1));
        assertEquals("dev.knight", p.getCard(2));

        // out of range indices return empty string
        assertEquals("", p.getCard(-1));
        assertEquals("", p.getCard(3));

        // hasCard, countCards
        assertTrue(p.hasCard("dev.knight"));
        assertTrue(p.hasCard("dev.victory_point"));
        assertEquals(2, p.countCards("dev.knight"));
        assertEquals(0, p.countCards("dev.road_building"));
    }

    @Test
    public void structures_operations() {
        Player p = new Player("StructPlayer");

        // not depleted initially for a known type
        assertFalse(p.depletedStructures("player_infrastructure.road"));
        assertEquals(15, p.getStructuresRemaining("player_infrastructure.road"));

        // change structures by valid negative amount
        assertTrue(p.changeStructuresRemainingByType("player_infrastructure.road", -5));
        assertEquals(10, p.getStructuresRemaining("player_infrastructure.road"));

        // cannot change to negative total
        assertFalse(p.changeStructuresRemainingByType("player_infrastructure.road", -20));
        assertEquals(10, p.getStructuresRemaining("player_infrastructure.road"));

        // invalid type operations
        assertFalse(p.changeStructuresRemainingByType("unknown.type", -1));
        assertFalse(p.setStructuresRemainingByType("unknown.type", 2));
        assertEquals(0, p.getStructuresRemaining("unknown.type"));
        assertTrue(p.depletedStructures("unknown.type")); // unknown considered depleted

        // set structures remaining valid
        assertTrue(p.setStructuresRemainingByType("player_infrastructure.road", 2));
        assertEquals(2, p.getStructuresRemaining("player_infrastructure.road"));

        // replaceStructuresRemaining rejects null
        assertFalse(p.replaceStructuresRemaining(null));

        // replace with new map
        HashMap<String, Integer> newMap = new HashMap<>();
        newMap.put("player_infrastructure.road", 1);
        newMap.put("player_infrastructure.settlement", 0);
        assertTrue(p.replaceStructuresRemaining(newMap));
        assertEquals(1, p.getStructuresRemaining("player_infrastructure.road"));
        assertEquals(0, p.getStructuresRemaining("player_infrastructure.settlement"));

        // emptyStructuresRemaining sets all to 0
        p.emptyStructuresRemaining();
        assertEquals(0, p.getStructuresRemaining("player_infrastructure.road"));
        assertEquals(0, p.getStructuresRemaining("player_infrastructure.settlement"));
        assertTrue(p.depletedStructures("player_infrastructure.road"));
        assertTrue(p.depletedStructures("player_infrastructure.settlement"));
    }

    @Test
    public void toString_containsImportantInfo() {
        Player p = new Player("Stringy");
        p.addCard("dev.foo");
        p.setResourceCount(ResourceRegistry.getInstance().get("resource.wood"), 2);
        String s = p.toString();
        assertTrue(s.contains("Stringy"));
        // removed resourceID, as ResourceConfig does not have toString implemented
        assertTrue(s.contains("dev.foo"));
    }
}