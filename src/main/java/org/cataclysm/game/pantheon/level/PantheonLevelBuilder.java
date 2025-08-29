package org.cataclysm.game.pantheon.level;

import org.bukkit.*;
import org.cataclysm.game.world.generator.VoidGenerator;

public class PantheonLevelBuilder {
    public static World getOrCreateWorld() {
        String worldID = "world_pantheon";
        World world = Bukkit.getWorld(worldID);
        if (world == null) {
            WorldCreator wc = new WorldCreator(worldID);
            wc.generator(new VoidGenerator());
            wc.environment(World.Environment.NORMAL);
            world = wc.createWorld();
        }
        if (world != null) {
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setDifficulty(Difficulty.HARD);
        }
        return world;
    }
}
