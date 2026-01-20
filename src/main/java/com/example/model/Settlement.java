package com.example.model;

import com.example.model.config.PlayerInfrastructureConfig;
import com.example.model.config.service.ConfigService;

public class Settlement {

    private int playerID;

    private int vertex;

    //is this fine as a string or should i make it an enum
    private String settlementID;

    //do i need if it is blocked by robber or is that established elsewhere??

    public Settlement(){}

    public Settlement(int _playerID, int _vertex){
        this.playerID = _playerID;
        this.vertex = _vertex;
        this.settlementID = "player_infrastructure.settlement";
    }

    public int getPlayerID(){return this.playerID;}
    public int getVertex(){return this.vertex;}
    public String getSettlementType(){return this.settlementID;}

    public void setPlayerID(int _playerID){this.playerID = _playerID;}
    public void setVertices(int _vertex){this.vertex = _vertex;}

    //do i need to check the resources in the function?
    //probably not
    public boolean upgradeSettlementType(){
        if (this.settlementID.equals("player_infrastructure.settlement")){
            this.settlementID ="player_infrastructure.city";
            return true;
        }
        return false;
    }

    //can you go from city to settlement??

    //are these points correct?
    public int getVictoryPoints(){
        PlayerInfrastructureConfig thisConfig = ConfigService.getInfrastructure(settlementID);
        return thisConfig.victoryPoints;
    }

}
