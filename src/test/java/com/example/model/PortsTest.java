package com.example.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.model.config.ConfigManager;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class PortsTest {

    @BeforeEach
    public void initConfig() {
        ConfigManager.loadAll();
    }

    @AfterEach
    public void closeConfig() {
        ConfigManager.unloadAll();
    }

    @Test
    public void constructor_initializesPortsCollection() {
        Ports ports = new Ports();
        ArrayList<Port> portList = getPortsViaReflection(ports);

        assertNotNull(portList);
    }

    @Test
    public void portsListSizeIsAtLeastNumberOfVertexSlots() {
        Ports ports = new Ports();
        ArrayList<Port> portList = getPortsViaReflection(ports);

        // Must have enough ports to assign vertices safely
        assertTrue(portList.size() >= 9,
                "Not enough ports to assign all vertex positions");
    }

    @Test
    public void allPortsHavePortIDsAssigned() {
        Ports ports = new Ports();
        ArrayList<Port> portList = getPortsViaReflection(ports);

        for (Port p : portList) {
            assertNotNull(p.getPortID());
            assertFalse(p.getPortID().isEmpty());
        }
    }

    @Test
    public void firstPortsHaveAdjacentVerticesAssigned() {
        Ports ports = new Ports();
        ArrayList<Port> portList = getPortsViaReflection(ports);

        // Only check ports that are guaranteed to be assigned vertices
        int portsWithVertices = Math.min(portList.size(), 9);

        for (int i = 0; i < portsWithVertices; i++) {
            assertNotNull(portList.get(i).getAdjVertices());
            assertEquals(2, portList.get(i).getAdjVertices().length);
        }
    }

    @Test
    public void remainingPortsHaveNoVerticesAssigned() {
        Ports ports = new Ports();
        ArrayList<Port> portList = getPortsViaReflection(ports);

        for (int i = 9; i < portList.size(); i++) {
            assertNull(portList.get(i).getAdjVertices());
        }
    }

    @Test
    public void portsAreShuffledBeforeVertexAssignment() {
        Ports ports1 = new Ports();
        Ports ports2 = new Ports();

        ArrayList<Port> list1 = getPortsViaReflection(ports1);
        ArrayList<Port> list2 = getPortsViaReflection(ports2);

        int compareCount = Math.min(list1.size(), list2.size());
        boolean sameOrder = true;

        for (int i = 0; i < compareCount; i++) {
            if (!list1.get(i).getPortID().equals(list2.get(i).getPortID())) {
                sameOrder = false;
                break;
            }
        }

        // Probabilistic, but acceptable for documentation
        assertFalse(sameOrder);
    }

    /* ---------- reflection helper ---------- */

    @SuppressWarnings("unchecked")
    private ArrayList<Port> getPortsViaReflection(Ports ports) {
        try {
            Field field = Ports.class.getDeclaredField("ports");
            field.setAccessible(true);
            return (ArrayList<Port>) field.get(ports);
        } catch (Exception e) {
            fail("Could not access ports field via reflection");
            return null; // unreachable
        }
    }
}