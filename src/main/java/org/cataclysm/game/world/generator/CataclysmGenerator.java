package org.cataclysm.game.world.generator;

import org.bukkit.*;
import org.bukkit.generator.ChunkGenerator;
import org.cataclysm.game.world.generator.pale_void.PaleVoidGenerator;
import org.cataclysm.game.world.generator.the_end.EndGenerator;

public class CataclysmGenerator {
    public static void setUp() {
        setUpWorld("custom_end", new EndGenerator(), World.Environment.THE_END);
        setUpWorld("world_pale_void", new PaleVoidGenerator(), World.Environment.NORMAL);
        setUpWorld("world_pantheon", new VoidGenerator(), World.Environment.NORMAL);
    }

    public static void setUpWorld(String worldID, ChunkGenerator generator, World.Environment environment) {
        World world = Bukkit.getWorld(worldID);
        if (world == null) {
            WorldCreator creator = new WorldCreator(worldID);
            creator.generator(generator);
            creator.environment(environment);
            creator.generateStructures(false);
            creator.createWorld();
        }

        if (world != null) {
            if (world.getName().equalsIgnoreCase("world_pantheon")) world.setTime(18000);
            else if (world.getName().equalsIgnoreCase("world_pale_void")) world.setTime(0);

            world.setDifficulty(Difficulty.HARD);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }
    }
}
