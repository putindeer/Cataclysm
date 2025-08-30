package org.cataclysm.game.pantheon.level.levels;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;
import org.cataclysm.game.pantheon.level.levels.entrance.preparation.EntranceMob;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.game.world.generator.VoidGenerator;

public class LevelBuilder {
    public static void adapt() {
        handlePaleVoid(true);
        handleEntrance();
    }

    public static void restore() {
        handlePaleVoid(false);
    }

    public static void handleEntrance() {
        Location location = PantheonEntrance.getLocation();
        getSchemLoader("entrance").pasteSchematic(location, true);
        EntranceMob.handle(false);
        EntranceMob.handle(true);
    }

    private static SchematicLoader getSchemLoader(String path) {
        return new SchematicLoader("pantheon/schematics/" + path + ".schem");
    }

    public static void handlePaleVoid(boolean adapt) {
        World world = Dimensions.PALE_VOID.getWorld();
        if (world == null) return;

        world.setGameRule(GameRule.DO_WEATHER_CYCLE, !adapt);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, !adapt);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, !adapt);

        world.setTime(0);
        world.setClearWeatherDuration(100000000);
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