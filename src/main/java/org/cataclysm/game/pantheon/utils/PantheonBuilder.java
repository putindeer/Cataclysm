package org.cataclysm.game.pantheon.utils;

import org.bukkit.*;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.enums.PantheonPhases;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.world.generator.VoidGenerator;

public class PantheonBuilder {
    public static PantheonOfCataclysm buildPantheon(boolean pasteStructure) {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        pantheon.getHandler().changePhase(PantheonPhases.IDDLE);
        if (pasteStructure) pastePantheonEntrance();
        return pantheon;
    }

    private static void pastePantheonEntrance() {
        Location location = PantheonLevels.PANTHEON_ENTRANCE.getCoreLocation();
        SchematicLoader schemLoader = getSchemLoader("entrance");
        schemLoader.pasteSchematic(location, true);
    }

    private static SchematicLoader getSchemLoader(String path) {
        return new SchematicLoader("pantheon/schematics/" + path + ".schem");
    }

    public static World buildWorld() {
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