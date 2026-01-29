package com.example.model;
public class Tile {

    //the id of the tile
    private String tileID;
    //the number that needs rolled to produce said resource
    private int number;
    //stores every possible location for a settlement
    private int[] adjVertices;
    //if blocked by a robber, true
    private boolean isBlocked;
    //if destroyed by a disaster card, true
    private boolean isDestroyed;

    //default constructor
    public Tile() {}

    //getters
    public String getTileID(){ return this.tileID; }
    public int getNumber(){ return this.number; }
    public int[] getAdjVertices(){ return this.adjVertices; }
    public boolean getIsBlocked(){ return this.isBlocked; }
    public boolean getIsDestroyed(){ return this.isDestroyed; }

    //setters
    public void setTileID(String _tileID){ this.tileID = _tileID;}
    public void setNumber(int _number){ this.number = _number;}
    public void setAdjVertices(int[] _adjVertices){ this.adjVertices = _adjVertices;}
    public void setIsBlocked(boolean _isBlocked){ this.isBlocked = _isBlocked;}
    public void setIsDestroyed(boolean _isDestroyed){ this.isDestroyed = _isDestroyed;}
}
