package org.cataclysm.game.world;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.util.NumberConversions;

@Getter
public enum Dimensions {
    BETA(new WorldCreator("world_beta").type(WorldType.FLAT)),
    PANTHEON(new WorldCreator("world_pantheon")),

    OVERWORLD(new WorldCreator("world")),
    NETHER(new WorldCreator("world_nether")),
    THE_END(new WorldCreator("custom_end")),
    PALE_VOID(new WorldCreator("world_pale_void")),
    ;

    private final WorldCreator worldCreator;

    Dimensions(WorldCreator worldCreator) {
        this.worldCreator = worldCreator;
    }

    public World getWorld() {
        return Bukkit.getWorld(worldCreator.name());
    }

    public World createWorld() {
        World world = this.worldCreator.createWorld();
        if (world == null) return Bukkit.getWorld(this.worldCreator.name());
        if (this == PANTHEON) {
            world.setTime(18000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }
        return world;
    }

    public double getDistanceFromSpawn(Location location) {
        var spawnLocation = this.createWorld().getSpawnLocation();
        int spawnX = spawnLocation.getBlockX();
        int spawnZ = spawnLocation.getBlockZ();
        int otherX = location.getBlockX();
        int otherZ = location.getBlockZ();
        return NumberConversions.square(spawnX - otherX) + NumberConversions.square(spawnZ - otherZ);
    }
}
