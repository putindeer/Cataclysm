package org.cataclysm.game.pantheon.level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.cataclysm.game.world.generator.VoidGenerator;

public class LevelManager {
    private static final String worldID = "world_pantheon";

    public static World createWorld() {
        World world = Bukkit.getWorld(worldID);
        if (world == null) {
            WorldCreator wc = new WorldCreator("world_pantheon");
            wc.generator(new VoidGenerator());
            wc.environment(World.Environment.NORMAL);
            world = wc.createWorld();
        }
        return world;
    }
}
