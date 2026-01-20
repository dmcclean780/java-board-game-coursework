package com.example.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Roads Class; stores all road objects and manages road building
 * @author 40452739
 */
public class Roads {

    public static int minimumLongestRoadLength = 3; // minimum length for longest road
    
    public static final int NUMBER_OF_ROADS = 72; // number of unique edges on the board
    public static final int UNOWNED_ROAD_ID = -1;
    
    private int nextBuildID = 1; // ID to assign to the next built road

    private Road[] roads; // array of all road objects; starts filled with unowned roads

    public Roads() {
        roads = new Road[NUMBER_OF_ROADS];

        for (int i = 0; i < NUMBER_OF_ROADS; i++) {
            roads[i] = new Road(Roads.UNOWNED_ROAD_ID, Roads.roadConnections[i], 0);
        }
    }
    
    /**
     * Gets all roads that are currently owned by players
     * @return array of owned Road objects
     */
    public Road[] getAllRoads() {
        // Only returns owned roads
        int NUMBER_OF_OWNED_ROADS = 0;
        for (int i = 0; i < NUMBER_OF_ROADS; i++) {
            if (roads[i].getPlayerID() != UNOWNED_ROAD_ID) {
                NUMBER_OF_OWNED_ROADS++;
            }
        }
        Road[] ownedRoads = new Road[NUMBER_OF_OWNED_ROADS];
        for (int i = 0, j = 0; i < NUMBER_OF_ROADS; i++) {
            if (roads[i].getPlayerID() != UNOWNED_ROAD_ID) {
                ownedRoads[j++] = roads[i];
            }
        }
        return ownedRoads;
    }

    /**
     * Attempts to build a road at the specified index for the given player.
     * @param index     Index of the road to build (0 to 71)
     * @param playerID  ID of the player building the road
     * @return          true if the road was successfully built; false otherwise
     */
    public boolean buildRoad(int index, int playerID) {
        if (isValidRoadIndex(index)) {
            if (roads[index].getPlayerID() == UNOWNED_ROAD_ID) {
                roads[index].setPlayerID(playerID);
                roads[index].setBuildID(nextBuildID++);
                
                return true;
            }
            return false; // road already owned
        }
        return false; // invalid index
    }

    /**
     * Attempts to build a road at the specified index for the given player.
     * @param index     Index of the road to build (0 to 71)
     * @param playerID  ID of the player building the road
     * @return          true if the road was successfully built; false otherwise
     */
    public boolean buildRoad(int vertex1, int vertex2, int playerID) {
        if (playerID == UNOWNED_ROAD_ID) {
            return false; // cannot build road for unowned ID
        }
        int index = getRoadIndex(vertex1, vertex2);
        return buildRoad(index, playerID);
    }

    /**
     * Gets the player ID who owns the road at the specified index
     * @param index index of the road (0 to 71)
     * @return the player ID who owns the road, UNOWNED_ROAD_ID if unowned, or throws exception if invalid index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public int ownedByPlayer(int index) {
        if (isValidRoadIndex(index)) {
            return roads[index].getPlayerID();
        }
        throw new IndexOutOfBoundsException("Invalid road index: " + index);
    }

    /**
     * Gets the player ID who owns the road at the specified vertices
     * @param vertex1 first vertex of the road
     * @param vertex2 second vertex of the road
     * @return the player ID who owns the road, UNOWNED_ROAD_ID if unowned, or throws exception if invalid vertices
     * @throws IndexOutOfBoundsException if the vertices are invalid
     */
    public int ownedByPlayer(int vertex1, int vertex2) {
        int index = getRoadIndex(vertex1, vertex2);
        return ownedByPlayer(index);
    }

    /**
     * Checks if the road at the specified index is owned by any player
     * @param index index of the road (0 to 71)
     * @return true if the road is owned; false if unowned
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public boolean isRoadOwned(int index) {
        if (isValidRoadIndex(index)) {
            return roads[index].getPlayerID() != UNOWNED_ROAD_ID;
        }
        throw new IndexOutOfBoundsException("Invalid road index: " + index);
    }

    /**
     * Checks if the road at the specified vertices is owned by any player
     * @param vertex1 first vertex of the road
     * @param vertex2 second vertex of the road
     * @return true if the road is owned; false if unowned
     * @throws IndexOutOfBoundsException if the vertices are invalid
     */
    public boolean isRoadOwned(int vertex1, int vertex2) {
        int index = getRoadIndex(vertex1, vertex2);
        return isRoadOwned(index);
    }


    /**
     * Attempts to remove a road at the specified index for the given player.
     * @param index     Index of the road to remove (0 to 71)
     * @return          true if the road was successfully removed; false otherwise
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public boolean removeRoad(int index) {
        if (!isValidRoadIndex(index)) {
            throw new IndexOutOfBoundsException("Invalid road index: " + index);
        }

        if (roads[index].getPlayerID() == UNOWNED_ROAD_ID) {
            return false; // road is already unowned
        }
        roads[index].setPlayerID(UNOWNED_ROAD_ID);
        return true;
    }

        /**
     * Attempts to remove a road at the specified vertices for the given player.
     * @param vertex1   First vertex of the road to remove
     * @param vertex2   Second vertex of the road to remove
     * @return          true if the road was successfully removed; false otherwise
     * @throws IndexOutOfBoundsException if the vertices are invalid
     */
    public boolean removeRoad(int vertex1, int vertex2) {
        int index = getRoadIndex(vertex1, vertex2);
        return removeRoad(index);
    }

    /**
     * Helper function; compares two roads to see which is better for longest road calculation
     * @param lenA length of road A
     * @param maxBuildA maximum build ID on road A
     * @param lenB length of road B
     * @param maxBuildB maximum build ID on road B
     * @return whether road A is better than road B
     */
    private static boolean isBetterRoad(int lenA, int maxBuildA, int lenB, int maxBuildB) {
        if (lenA != lenB) return lenA > lenB;
        return maxBuildA < maxBuildB;
    }

    /**
     * Helper struct; stores result of a path search
     */
    private static class PathResult {
        int length;
        int maxBuildID;

        PathResult(int length, int maxBuildID) {
            this.length = length;
            this.maxBuildID = maxBuildID;
        }
    }

    /**
     * Helper function; builds adjacency list of roads for the given player
     * @param playerID ID of the player
     * @return adjacency list mapping vertex to list of road indices
     */
    private Map<Integer, List<Integer>> buildAdjacencyForPlayer(int playerID) {
        Map<Integer, List<Integer>> adj = new HashMap<>();

        for (int i = 0; i < NUMBER_OF_ROADS; i++) {
            Road r = roads[i];
            if (r.getPlayerID() == playerID) {
                int v1 = r.getVertices()[0];
                int v2 = r.getVertices()[1];

                adj.computeIfAbsent(v1, k -> new ArrayList<>()).add(i);
                adj.computeIfAbsent(v2, k -> new ArrayList<>()).add(i);
            }
        }
        return adj;
    }

    /**
     * Helper function; performs DFS to find the longest road from the current vertex
     * WARNING: recursive function
     * @param currentVertex current vertex in the DFS
     * @param adj adjacency list of roads
     * @param usedEdge array tracking used edges
     * @param currentLength current length of the road
     * @param currentMaxBuild current maximum build ID on the road
     * @param best best path result found so far
     */
    private void dfsLongestRoad(int currentVertex, Map<Integer, List<Integer>> adj, boolean[] usedEdge, int currentLength, int currentMaxBuild, PathResult best) {
        // Update best result
        if (isBetterRoad(currentLength, currentMaxBuild, best.length, best.maxBuildID)) {
            best.length = currentLength;
            best.maxBuildID = currentMaxBuild;
        }

        List<Integer> edges = adj.get(currentVertex);
        if (edges == null) return;

        for (int edgeIndex : edges) {
            if (usedEdge[edgeIndex]) continue;

            Road r = roads[edgeIndex];
            int[] v = r.getVertices();
            int nextVertex = (v[0] == currentVertex) ? v[1] : v[0];

            usedEdge[edgeIndex] = true;
            dfsLongestRoad(
                    nextVertex,
                    adj,
                    usedEdge,
                    currentLength + 1,
                    Math.max(currentMaxBuild, r.getBuildID()),
                    best
            );
            usedEdge[edgeIndex] = false;
        }
    }

    /**
     * Gets the best (longest) road for the specified player
     * @param playerID ID of the player
     * @return PathResult containing length and max build ID of the best road
     */
    private PathResult getBestRoadForPlayer(int playerID) {
        Map<Integer, List<Integer>> adj = buildAdjacencyForPlayer(playerID);
        PathResult best = new PathResult(0, Integer.MAX_VALUE);
        boolean[] usedEdge = new boolean[NUMBER_OF_ROADS];

        for (Map.Entry<Integer, List<Integer>> entry : adj.entrySet()) {
            int vertex = entry.getKey();
            dfsLongestRoad(vertex, adj, usedEdge, 0, 0, best);
        }
        return best;
    }

    /**
     * Gets the player ID who currently owns the longest road
     * @return player ID of the longest road owner, or UNOWNED_ROAD_ID if none
     */
    public int longestRoadOwner(int[] playerIDs) {
        int bestPlayer = UNOWNED_ROAD_ID;
        int bestLength = 0;
        int bestMaxBuild = Integer.MAX_VALUE;

        for (int playerID : playerIDs) {
            PathResult r = getBestRoadForPlayer(playerID);

            if (r.length < minimumLongestRoadLength) continue;

            if (isBetterRoad(r.length, r.maxBuildID, bestLength, bestMaxBuild)) {
                bestLength = r.length;
                bestMaxBuild = r.maxBuildID;
                bestPlayer = playerID;
            }
        }
        return bestPlayer;
    }

    /**
     * Checks if there is a longest road currently owned by any player
     * @return whether a longest road exists
     */
    public boolean longestRoadExists(int[] playerIDs) {
        return longestRoadOwner(playerIDs) != UNOWNED_ROAD_ID;
    }

    /**
     * Gets the length of the current longest road owned by any player
     * @return length of the longest road
     */
    public int getLongestRoadLength(int[] playerIDs) {
        int best = 0;
        for (int playerID : playerIDs) {
            best = Math.max(best, getBestRoadForPlayer(playerID).length);
        }
        return best;
    }

    /**
     * Helper function; gets the road index for the given vertices
     * @param vertex1 first vertex of the road
     * @param vertex2 second vertex of the road
     * @return the road index, or -1 if invalid vertices
     */
    public static int getRoadIndex(int vertex1, int vertex2) {
        if (vertex1 > vertex2) { // swap to maintain order; vertex1 <= vertex2
            int temp = vertex1;
            vertex1 = vertex2;
            vertex2 = temp;
        }
        for (int i = 0; i < NUMBER_OF_ROADS; i++) {
            int[] roadVertices = Roads.roadConnections[i];
            if ((roadVertices[0] == vertex1 && roadVertices[1] == vertex2)) {
                return i;
            }
        }
        return -1; // invalid vertices
    }

    // Helper function; returns if the road index is valid
    public static boolean isValidRoadIndex(int index) {
        return index >= 0 && index < NUMBER_OF_ROADS;
    }

    
    // Helper function; returns the given road vertices are valid
    public static boolean isValidVertices(int vertex1, int vertex2) {
        if (vertex1 > vertex2) { // swap to maintain order; vertex1 <= vertex2
            int temp = vertex1;
            vertex1 = vertex2;
            vertex2 = temp;
        }
        for (int i = 0; i < NUMBER_OF_ROADS; i++) {
            int[] roadVertices = roadConnections[i];
            if ((roadVertices[0] == vertex1 && roadVertices[1] == vertex2)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVertexLinkedByRoad(int vertex, int playerID){
        return true; // TODO: Implement road linkage check
    }


    // the two vertices that each road connects; 72 roads total
    public static final int[][] roadConnections = {
        {0, 1}, {1, 2}, {2, 3}, {3, 4}, {4, 5}, {5, 6}, // 1st line
        {0, 8}, {2, 10}, {4, 12}, {6, 14}, // 1st verticals
        {7, 8}, {8, 9}, {9, 10}, {10, 11}, {11, 12}, {12, 13}, {13, 14}, {14, 15}, // 2nd line
        {7, 17}, {9, 19}, {11, 21}, {13, 23}, {15, 25}, // 2nd verticals
        {16, 17}, {17, 18}, {18, 19}, {19, 20}, {20, 21}, {21, 22}, {22, 23}, {23, 24}, {24, 25}, {25, 26}, // 3rd line
        {16, 27}, {18, 29}, {20, 31}, {22, 33}, {24, 35}, {26, 37}, // 3rd verticals
        {27, 28}, {28, 29}, {29, 30}, {30, 31}, {31, 32}, {32, 33}, {33, 34}, {34, 35}, {35, 36}, {36, 37}, // 4th line
        {28, 38}, {30, 40}, {32, 42}, {34, 44}, {36, 46}, // 4th verticals
        {38, 39}, {39, 40}, {40, 41}, {41, 42}, {42, 43}, {43, 44}, {44, 45}, {45, 46}, // 5th line
        {39, 47}, {41, 49}, {43, 51}, {45, 53},  // 5th verticals
        {47, 48}, {48, 49}, {49, 50}, {50, 51}, {51, 52}, {52, 53}  // 6th (final) line
    };

    public static int[][] getRoadConnections()
    {
        return roadConnections;
    }
}