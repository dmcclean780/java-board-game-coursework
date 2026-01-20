package com.example.model;

public class Settlements {

    public static final int NUMBER_OF_VERTICES = 54;
    public static final int UNOWNED_SETTLEMENT_ID = -1;

    private Settlement[] settlements;

    public Settlements(){

        settlements = new Settlement[NUMBER_OF_VERTICES];

        for (int i = 0; i < NUMBER_OF_VERTICES; i++){
            settlements[i] = new Settlement(UNOWNED_SETTLEMENT_ID, i);
        }
    }

    public boolean buildSettlement(int vertex, int playerID){

        if (!isValidVertex(vertex)){
            return false;
        }

        if (settlements[vertex].getPlayerID() != UNOWNED_SETTLEMENT_ID){
            return false;
        }

        settlements[vertex].setPlayerID(playerID);

        return true;

    }

    //do you need to be able to remove settlements??

    public int ownedByPlayer(int vertex){

        if (!isValidVertex(vertex)){
            throw new IndexOutOfBoundsException("Invalid vertex: " + vertex);
        }

        return settlements[vertex].getPlayerID();

    }

    public boolean upgradeSettlement(int vertex, int playerID){
        
        if (!isValidVertex(vertex)){
            return false;
        }

        Settlement s = settlements[vertex];

        if (s.getPlayerID() != playerID){
            return false;
        }

        return s.upgradeSettlementType();

    }

    public Settlement[] getAllOwnedSettlements(){

        int count = 0;

        for (Settlement s : settlements){
            if (s.getPlayerID() != UNOWNED_SETTLEMENT_ID){
                count++;
            }
        }

        Settlement[] ownedSettlements = new Settlement[count];
        int index = 0;

        for (Settlement s : settlements){
            if (s.getPlayerID() != UNOWNED_SETTLEMENT_ID){
                ownedSettlements[index++] = s;
            }
        }

        return ownedSettlements;

    }

    public Settlement[] getAllSettlements(){
        return settlements;
    }

    public int getVictoryPoints(int playerID){

        int points = 0;

        for (Settlement s : settlements){
            if (s.getPlayerID() == playerID){
                points += s.getVictoryPoints();
            }
        }

        return points;

    }

    public static boolean isValidVertex(int vertex){
        return vertex >= 0 && vertex < NUMBER_OF_VERTICES;
    }

    /**
     * Checks if a potential settlement (at a vertex) is near another settlement.
     * @param vertex vertex to check
     * @return whether there are any settlements near the vertex with a range of one
     */
    public boolean nearbySettlement(int vertex){
        if (settlements[vertex].getPlayerID() == UNOWNED_SETTLEMENT_ID){
            return false;
        }

        if (!isValidVertex(vertex)){
            return false;
        }

        for (Settlement s : settlements) {
            if (s.getPlayerID() == Settlements.UNOWNED_SETTLEMENT_ID) {
                continue;
            }
            if (s.getVertex() == vertex) {
                continue; // skip if it is the current vertex
            }

            int svertex = s.getVertex();

            for (int[] adj : AdjacencyMaps.RoadConnections) {
                if ((adj[0] == vertex || adj[1] == vertex) && (adj[0] == svertex || adj[1] == svertex)) {
                    return false;
                }
            }
        }

        return true;

    }

}
