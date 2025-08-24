package org.cataclysm.game.world.generator;

import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.cataclysm.game.world.generator.pale_void.PaleVoidGenerator;
import org.cataclysm.game.world.generator.the_end.EndGenerator;

import java.util.Objects;

public class CataclysmGenerator {
    public static void setUp() {
        setUpWorld("custom_end", new EndGenerator(), World.Environment.THE_END);
        setUpWorld("world_pale_void", new PaleVoidGenerator(), World.Environment.NORMAL);
    }

    public static void setUpWorld(String worldID, ChunkGenerator generator, World.Environment environment) {
        World world = Bukkit.getWorld(worldID);
        if (world == null) {
            WorldCreator creator = new WorldCreator(worldID);
            creator.generator(generator);
            creator.environment(environment);
            creator.generateStructures(false);
            creator.createWorld();
        } else world.setDifficulty(Difficulty.HARD);
    }
}
