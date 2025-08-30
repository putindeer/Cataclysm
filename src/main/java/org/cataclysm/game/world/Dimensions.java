package org.cataclysm.game.world;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.util.NumberConversions;
import org.cataclysm.game.world.generator.VoidGenerator;

@Getter
public enum Dimensions {
    BETA(new WorldCreator("world_beta").type(WorldType.FLAT)),
    PANTHEON(new WorldCreator("world_pantheon").generator(new VoidGenerator())),

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
        return this.worldCreator.createWorld();
    }

    public double getDistanceFromSpawn(Location location) {
        var spawnLocation = this.getWorld().getSpawnLocation();
        int spawnX = spawnLocation.getBlockX();
        int spawnZ = spawnLocation.getBlockZ();
        int otherX = location.getBlockX();
        int otherZ = location.getBlockZ();
        return NumberConversions.square(spawnX - otherX) + NumberConversions.square(spawnZ - otherZ);
    }
}
