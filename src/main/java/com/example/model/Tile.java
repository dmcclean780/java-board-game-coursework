package com.example.model;

import java.util.Collection;

import com.example.model.config.ResourceConfig;
import com.example.model.config.service.ConfigService;

public class Tile {

    //the id of the tile
    private String tileID;
    //the number that needs rolled to produce said resource
    private int number;
    //stores every possible location for a settlement
    private int[] adjVertices;
    //if blocked by a robber, true
    private boolean isBlocked;

    //default constructor
    public Tile() {}

    //getters
    public String getTileID(){ return this.tileID; }
    public int getNumber(){ return this.number; }
    public int[] getAdjVertices(){ return this.adjVertices; }
    public boolean getIsBlocked(){ return this.isBlocked; }

    //setters
    public void setTileID(String _tileID){ this.tileID = _tileID;}
    public void setNumber(int _number){ this.number = _number;}
    public void setAdjVertices(int[] _adjVertices){ this.adjVertices = _adjVertices;}
    public void setIsBlocked(boolean _isBlocked){ this.isBlocked = _isBlocked;}

    public ResourceConfig getResourceFromTileID(){
        Collection<ResourceConfig> allResources = ConfigService.getAllResources();
        for (ResourceConfig resource : allResources){
            if (resource.id.equals(tileID)){
                //found resource
                return resource;
            }
        }
        //failed
        return null;
    }
}
