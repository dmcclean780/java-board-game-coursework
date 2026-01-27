package com.example.model.config.service;

import com.example.model.config.*;
import com.example.model.config.registry.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class ConfigService {

    // -------------------------
    // Game Rules
    // -------------------------
    public static GameRulesConfig getGameRules() {
        return GameRulesRegistry.get();
    }

    public static int getVictoryPointsToWin() {
        return GameRulesRegistry.get().victoryPointsToWin;
    }

    public static int getRobberCardLimit() {
        return GameRulesRegistry.get().robberCardLimit;
    }

    public static Map<Integer, Integer> getNumberTokens(){
        return GameRulesRegistry.get().numberTokens;
    }

    // -------------------------
    // Resource Config
    // -------------------------
    public static ResourceConfig getResource(String id) {
        return ResourceRegistry.getInstance().get(id);
    }

    public static Collection<ResourceConfig> getAllResources() {
        return ResourceRegistry.getInstance().all();
    }

    // -------------------------
    // Dev Card Config
    // -------------------------
    public static DevCardConfig getDevCard(String id) {
        return DevCardRegistry.getInstance().get(id);
    }

    public static Collection<DevCardConfig> getAllDevCards() {
        return DevCardRegistry.getInstance().all();
    }

    // -------------------------
    // Port Config
    // -------------------------
    public static PortConfig getPort(String id) {
        return PortRegistry.getInstance().get(id);
    }

    public static Collection<PortConfig> getAllPorts() {
        return PortRegistry.getInstance().all();
    }

    // -------------------------
    // Player Infrastructure Config
    // -------------------------
    public static PlayerInfrastructureConfig getInfrastructure(String id) {
        return PlayerInfrastructureRegistry.getInstance().get(id);
    }

    public static Collection<PlayerInfrastructureConfig> getAllInfrastructure() {
        return PlayerInfrastructureRegistry.getInstance().all();
    }

    public static ArrayList<String> getAllInfrastructureIDs(){
        Collection<PlayerInfrastructureConfig> infrastructureConfigs = ConfigService.getAllInfrastructure();
        ArrayList<String> infrastructureIDs = new ArrayList<String>();
        for (PlayerInfrastructureConfig infrastructureConfig : infrastructureConfigs){
            infrastructureIDs.add(infrastructureConfig.id);
        }
        return infrastructureIDs;
    }

    // -------------------------
    //  Tile Config
    // -------------------------
    public static TileConfig getTile(String id) {
        return TileRegistry.getInstance().get(id);
    }

    public static Collection<TileConfig> getAllTiles() {
        return TileRegistry.getInstance().all();
    }

    public static ArrayList<String> getAllTileIDs(){
        Collection<TileConfig> tileConfigs = ConfigService.getAllTiles();
        ArrayList<String> tileIDs = new ArrayList<String>();
        for (TileConfig tileConfig : tileConfigs){
            tileIDs.add(tileConfig.id);
        }
        return tileIDs;
    }

    // -------------------------
    // Language Functions for Config Items
    // -------------------------
    public static String getDisplayName(String id) {
        if (id == null || id.equals("")) {
            return "NONE";
        }
        return LangManager.get(id + ".name");
    }

    public static String getDevCardDescription(String id) {
        if (id == null || id.equals("")) {
            return "NONE";
        }
        return LangManager.get(id + ".description");
    }

    // -------------------------
    // Disaster Card Config
    // -------------------------
    public static DisasterCardConfig getDisasterCard(String id) {
        return DisasterCardRegistry.getInstance().get(id);
    }

    public static Collection<DisasterCardConfig> getAllDisasterCards() {
        return DisasterCardRegistry.getInstance().all();
    }
}
