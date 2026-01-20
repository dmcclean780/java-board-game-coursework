package com.example.model;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Gameboard {
    //store array of tiles
    private Tiles tiles;

    //store all ports
    private Port[] ports;

    //store all player infrastructure
    private ArrayList<Structure> structures;

    //store all roads
    private ArrayList<Road> roads;

    
    //default constructor
    public Gameboard(){}


    //getters
    public Tiles getTiles(){return tiles;}
    public Port[] getPorts(){return ports;}
    public ArrayList<Structure> getStructures(){return structures;}
    public ArrayList<Road> getRoads(){return roads;}

    //setters
    public void setTiles(Tiles _tiles){tiles = _tiles;}
    public void setPorts(Port[] _ports){ports = _ports;}
    public void setStructures(ArrayList<Structure> _structures){structures = _structures;}
    public void setRoads(ArrayList<Road> _roads){roads = _roads;}

    //generate ports
    private void GeneratePorts(String jsonPath) throws IOException {

        //read in all port types from JSON
        ObjectMapper mapper = new ObjectMapper();
        List<Port> portTypes = mapper.readValue(new File(jsonPath), new com.fasterxml.jackson.core.type.TypeReference<List<Port>>() {});

        int NUMBER_OF_PORTS = 9;
        Port[] portArray = new Port[NUMBER_OF_PORTS];

        //for each port, give it an associated resource
        for (int i = 0; i < NUMBER_OF_PORTS; i++){
            
        }
    }
}
