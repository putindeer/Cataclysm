package org.cataclysm.game.mob.listener.spawn;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.vanilla.piglin.Kamikaze;
import org.cataclysm.game.mob.custom.vanilla.piglin.Knight;
import org.cataclysm.game.mob.custom.vanilla.piglin.Multishooter;
import org.cataclysm.game.mob.custom.vanilla.piglin.Pyrotechnic;
import org.cataclysm.game.world.Dimensions;

public class SpawnUtils {

    public static CataclysmMob getRandomPiglin(SpawnContext ctx) {
        CataclysmMob piglinToSpawn = null;
        switch (ctx.random.nextInt(4)) {
            case 0 -> piglinToSpawn = new Multishooter(ctx.level);
            case 1 -> piglinToSpawn = new Pyrotechnic(ctx.level);
            case 2 -> piglinToSpawn = new Knight(ctx.level);
            case 3 -> piglinToSpawn = new Kamikaze(ctx.level);
        }
        return piglinToSpawn;
    }

    public static void setMobCap(Entity entity, double maxPerPlayer, int maxPerChunk, double multiplier) {
        if (entity.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG) || entity.getEntitySpawnReason().equals(CreatureSpawnEvent.SpawnReason.COMMAND)) return;

        if (!entity.getLocation().getWorld().equals(Dimensions.THE_END.getWorld())){
           if (maxPerPlayer > 12) maxPerPlayer = 12;
           maxPerChunk = 2;
       }

        int playerCount = (int) entity.getWorld().getPlayers().stream().filter(player -> player.getGameMode() != GameMode.SPECTATOR).count();
        maxPerPlayer *= (multiplier * playerCount);

        if (entity.getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.SPAWNER) {
            setMobCap(entity, maxPerPlayer, maxPerChunk);
        }
    }

    public static void setMobCap(Entity entity, double maxPerPlayer, int maxPerChunk) {
        int chunkCount = 0;
        EntityType type = entity.getType();
        for (Entity ent : entity.getChunk().getEntities()) {
            if (type == ent.getType()) ++chunkCount;
        }

        if (chunkCount >= maxPerChunk || entity.getWorld().getEntitiesByClass(entity.getClass()).size() >= maxPerPlayer) entity.remove();
    }

    public static boolean isForestBiome(String biomeName) {
        return biomeName.contains("FOREST") || biomeName.contains("CHERRY") ||
                biomeName.equals("GROVE") || biomeName.contains("TAIGA") ||
                biomeName.contains("WOODED")
                || biomeName.contains("SAVANNA");
    }


}
