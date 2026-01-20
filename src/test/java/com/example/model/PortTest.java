package com.example.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PortTest {

    @Test
    public void defaultConstructor_initializesWithDefaults() {
        Port p = new Port();

        assertNull(p.getPortID());
        assertNull(p.getAdjVertices());
    }

    @Test
    public void parameterizedConstructor_initializesFieldsCorrectly() {
        int[] vertices = {1, 2, 3};
        Port p = new Port("port.wood", vertices);

        assertEquals("port.wood", p.getPortID());
        assertArrayEquals(vertices, p.getAdjVertices());
    }

    @Test
    public void setPortID_updatesValue() {
        Port p = new Port();

        p.setPortID("port.brick");
        assertEquals("port.brick", p.getPortID());

        p.setPortID(null);
        assertNull(p.getPortID());
    }

    @Test
    public void setAdjVertices_updatesValue() {
        Port p = new Port();

        int[] vertices = {10, 11};
        p.setAdjVertices(vertices);
        assertArrayEquals(vertices, p.getAdjVertices());

        p.setAdjVertices(null);
        assertNull(p.getAdjVertices());
    }

    @Test
    public void adjVertices_arrayIsNotCopied() {
        int[] vertices = {5, 6};
        Port p = new Port("port.generic", vertices);

        // Modify original array
        vertices[0] = 99;

        // Port reflects change (no defensive copy)
        assertEquals(99, p.getAdjVertices()[0]);
    }
}