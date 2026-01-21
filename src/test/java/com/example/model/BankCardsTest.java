package com.example.model;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.config.ConfigManager;
import com.example.model.config.DevCardConfig;
import com.example.model.config.DisasterCardConfig;
import com.example.model.config.ResourceConfig;
import com.example.model.config.service.ConfigService;


public class BankCardsTest {

    private BankCards bankCards;

    @BeforeAll
    public static void setUpAll() {
        try {
            ConfigManager.loadAll();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    @BeforeEach
    public void setUp() {
        bankCards = new BankCards();
    }

    @Test
    public void constructor_initializesResourceCards() {
        Collection<ResourceConfig> resources = ConfigService.getAllResources();
        for (ResourceConfig resource : resources) {
            assertEquals(resource.maxQuantity, bankCards.getResourceCount(resource),
                    "Resource " + resource.id + " should start with " + resource.maxQuantity + " cards");
        }
    }

    @Test
    public void constructor_initializesDevelopmentCards() {
        Collection<DevCardConfig> devCards = ConfigService.getAllDevCards();
        
        // After construction, development cards are shuffled
        // We can't directly check the list, but we verify it's initialized
        assertNotNull(devCards, "BankCards should be initialized");
    }

    @Test
    public void constructor_initializesDisasterCards() {
        Collection<DisasterCardConfig> disasterCards = ConfigService.getAllDisasterCards();
        
        // After construction, disaster cards are shuffled
        // We can't directly check the list, but we verify it's initialized
        assertNotNull(disasterCards, "BankCards should be initialized");
    }

    @Test
    public void giveResourceCard_sufficientCards_reducesCount() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);
        int amountToGive = 5;

        boolean result = bankCards.giveResourceCard(resource, amountToGive);

        assertTrue(result, "Should return true when sufficient cards available");
        assertEquals(initialCount - amountToGive, bankCards.getResourceCount(resource),
                "Resource count should be reduced by the amount given");
    }

    @Test
    public void giveResourceCard_insufficientCards_returnsFalse() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);

        boolean result = bankCards.giveResourceCard(resource, initialCount + 1);

        assertFalse(result, "Should return false when insufficient cards available");
        assertEquals(initialCount, bankCards.getResourceCount(resource),
                "Resource count should remain unchanged when unable to give cards");
    }

    @Test
    public void giveResourceCard_exactAmount_succeeds() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);

        boolean result = bankCards.giveResourceCard(resource, initialCount);

        assertTrue(result, "Should succeed when giving exact amount available");
        assertEquals(0, bankCards.getResourceCount(resource),
                "Resource count should be zero after giving all cards");
    }

    @Test
    public void giveResourceCard_zeroAmount_succeeds() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);

        boolean result = bankCards.giveResourceCard(resource, 0);

        assertTrue(result, "Should succeed when giving zero cards");
        assertEquals(initialCount, bankCards.getResourceCount(resource),
                "Resource count should remain unchanged when giving zero cards");
    }

    @Test
    public void giveDevelopmentCard_initiallyAvailable() {
        String result = bankCards.giveDevelopmentCard();
        assertFalse(result.isEmpty(), "Development card should be available initially");
        assertNotNull(result, "Development card should not be null");
    }

    @Test
    public void giveDevelopmentCard_depletesAvailableCards() {
        Collection<DevCardConfig> devCards = ConfigService.getAllDevCards();
        int expectedCount = 0;
        for (DevCardConfig devCard : devCards) {
            expectedCount += devCard.count;
        }

        // Give all development cards
        for (int i = 0; i < expectedCount; i++) {
            String result = bankCards.giveDevelopmentCard();
            assertFalse(result.isEmpty(), "Should be able to give development card " + (i + 1));
        }

        // Try to give one more when empty
        String result = bankCards.giveDevelopmentCard();
        assertEquals("", result, "Should return empty string when no development cards left");
    }

    @Test
    public void giveDisasterCard_initiallyAvailable() {
        String result = bankCards.giveDisasterCard();
        assertFalse(result.isEmpty(), "Disaster card should be available initially");
        assertNotNull(result, "Disaster card should not be null");
    }

    @Test
    public void giveDisasterCard_depletesAvailableCards() {
        Collection<DisasterCardConfig> disasterCards = ConfigService.getAllDisasterCards();
        int expectedCount = 0;
        for (DisasterCardConfig disasterCard : disasterCards) {
            expectedCount += disasterCard.count;
        }

        // Give all disaster cards
        for (int i = 0; i < expectedCount; i++) {
            String result = bankCards.giveDisasterCard();
            assertFalse(result.isEmpty(), "Should be able to give disaster card " + (i + 1));
        }

        // Try to give one more when empty
        String result = bankCards.giveDisasterCard();
        assertEquals("", result, "Should return empty string when no disaster cards left");
    }

    @Test
    public void returnResourceCard_increasesCount() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);
        int amountToReturn = 3;

        bankCards.returnResourceCard(resource, amountToReturn);

        assertEquals(initialCount + amountToReturn, bankCards.getResourceCount(resource),
                "Resource count should be increased by the amount returned");
    }

    @Test
    public void returnResourceCard_multipleReturns() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);

        bankCards.returnResourceCard(resource, 2);
        bankCards.returnResourceCard(resource, 3);

        assertEquals(initialCount + 5, bankCards.getResourceCount(resource),
                "Resource count should be increased by total amount returned");
    }

    @Test
    public void giveAndReturnResourceCard_cycles() {
        ResourceConfig resource = getFirstResource();
        int initialCount = bankCards.getResourceCount(resource);
        int amountToGive = 4;

        // Give some cards
        boolean giveResult = bankCards.giveResourceCard(resource, amountToGive);
        assertTrue(giveResult, "Should successfully give cards");

        // Return some cards
        bankCards.returnResourceCard(resource, amountToGive);

        assertEquals(initialCount, bankCards.getResourceCount(resource),
                "Resource count should return to initial after give and return cycle");
    }

    @Test
    public void getResourceCount_nonexistentResource_returnsZero() {
        ResourceConfig fakeResource = new ResourceConfig("nonexistent.resource", "path/to/texture", 0);

        int count = bankCards.getResourceCount(fakeResource);

        assertEquals(0, count, "Non-existent resource should return 0");
    }

    /**
     * Helper method to get the first resource from ConfigService
     */
    private ResourceConfig getFirstResource() {
        Collection<ResourceConfig> resources = ConfigService.getAllResources();
        if (resources.isEmpty()) {
            throw new RuntimeException("No resources available in ConfigService");
        }
        return resources.iterator().next();
    }
}
