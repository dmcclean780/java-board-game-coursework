package com.example.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.example.model.config.PortConfig;
import com.example.model.config.service.ConfigService;

public class Ports {

    private ArrayList<Port> ports;

    //generate ports
    public Ports() {

        //read in all port types from JSON
        Collection<PortConfig> portconfigs = ConfigService.getAllPorts();
        
        ports = new ArrayList<Port>();

        for (PortConfig i : portconfigs){
            for (int j = 0; j < i.occurrences; j++){
                Port newPort = new Port();
                newPort.setPortID(i.id);
                ports.add(newPort);
            }
        }

        //randomise order of ports
        Random rand = new Random();
        ports = shuffleArray(ports, rand);

        //port types all now set, vertices are next:
        ports = setAdjVerticesForEachPort(ports);
    }

    private static ArrayList<Port> shuffleArray(ArrayList<Port> array, Random rand) {
        for (int i = array.size() - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            Port temp = array.get(i);
            array.set(i, array.get(j));
            array.set(j, temp);
        }
        return array;
    }

    //sets all the vertices where ports are to be generated
    private static ArrayList<Port> setAdjVerticesForEachPort(ArrayList<Port> ports){
        for (int i = 0; i < VERTICES.length; i++){
            ports.get(i).setAdjVertices(VERTICES[i]);
        }
        
        return ports;
    }

    private static final int[][] VERTICES = {
        {0, 1},     // port 0
        {5, 6},     // port 1
        {14, 15},   // port 2
        {26, 37},   // port 3
        {45, 46},   // port 4
        {50, 51},   // port 5
        {47, 48},   // port 6
        {28, 38},   // port 7
        {7, 17}     // port 8
    };

    public static int[][] getPorts()
    {
        return VERTICES;
    }
}
