package com.example.model.config;

import com.example.model.config.registry.DevCardRegistry;
import com.example.model.config.registry.DisasterCardRegistry;
import com.example.model.config.registry.GameRulesRegistry;
import com.example.model.config.registry.PlayerInfrastructureRegistry;
import com.example.model.config.registry.PortRegistry;
import com.example.model.config.registry.ResourceRegistry;
import com.example.model.config.registry.TileRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;


public final class ConfigManager {

    private ConfigManager() {} // prevent instantiation

    /** Load all configs in the proper order */
    public static void loadAll() {
        ObjectMapper mapper = new ObjectMapper();

        // Load singleton GameRulesConfig first
        GameRulesRegistry.load(mapper, "/config/game_rules.json");

        // Load registries
        ResourceRegistry.getInstance().load(mapper);
        DevCardRegistry.getInstance().load(mapper);
        PortRegistry.getInstance().load(mapper);
        PlayerInfrastructureRegistry.getInstance().load(mapper);
        TileRegistry.getInstance().load(mapper);
        DisasterCardRegistry.getInstance().load(mapper);

        LangManager.load(mapper, "en_us");

        // Optional: validate cross-references
        // validateConfigs();
    }

    public static void unloadAll() {
        GameRulesRegistry.unload();
        ResourceRegistry.getInstance().unload();
        DevCardRegistry.getInstance().unload();
        PortRegistry.getInstance().unload();
        PlayerInfrastructureRegistry.getInstance().unload();
        TileRegistry.getInstance().unload();
        DisasterCardRegistry.getInstance().unload();
    }

    /** Validate that all configs are internally consistent */
    // private static void validateConfigs() {
    //     // Example: ensure ports reference valid resources
    //     PortRegistry.getInstance().all().forEach(port -> {
    //         if (port.resourceId != null && ResourceRegistry.getInstance().get(port.resourceId) == null) {
    //             throw new IllegalStateException("Port references invalid resource ID: " + port.resourceId);
    //         }
    //     });

    //     // Example: ensure dev cards reference valid actions/resources
    //     DevCardRegistry.getInstance().all().forEach(card -> {
    //         // ... validation logic here
    //     });

    //     // Example: ensure game rules make sense
    //     GameRulesConfig rules = GameRules.get();
    //     if (rules.numberTokens.values().stream().mapToInt(Integer::intValue).sum() != BoardConfig.TILE_COUNT) {
    //         throw new IllegalStateException("Number tokens in game rules do not match board tiles");
    //     }
    // }
}
