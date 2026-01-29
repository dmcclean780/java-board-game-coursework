package com.example.model;

public class Road {
    
    //player who owns this road
    private int playerID;

    private int buildID;

    //array of the two vertices the road connects
    private int[] vertices;

    private String roadType;

    //default constructor
    public Road(){}

    //parameterised constructor
    public Road(int _playerID, int[] _vertices, int _buildID){
        this.playerID = _playerID;
        this.vertices = _vertices;
        this.buildID = _buildID;
        this.roadType = "player_infrastructure.road";
    }

    //getters
    public int getPlayerID(){return this.playerID;}
    public int[] getVertices(){return this.vertices;}

    public int getBuildID(){return this.buildID;}


    //setters
    public void setPlayerID(int _playerID){this.playerID = _playerID;}
    public void setVertices(int[] _vertices){this.vertices = _vertices;}

    public void setBuildID(int _buildID){this.buildID = _buildID;}
    public String getRoadType() { return roadType; }

}
