package org.cataclysm.api.structure;


import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.MobLoader;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StructureManager {

    private static StructureManager instance;
    private final @Getter ScheduledExecutorService executorService;
    private final @Getter Set<CataclysmStructure> structures = ConcurrentHashMap.newKeySet();
    private final Set<String> targetMobIds = Set.of("headsman", "sentinel", "fishmoth", "arcanebreeze", "arcanespider");

    private StructureManager() {
        this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "CataclysmStructureManager");
            t.setDaemon(true);
            return t;
        });

        startTasks();
    }

    public static StructureManager getInstance() {
        if (instance == null) {
            instance = new StructureManager();
        }
        return instance;
    }

    public void registerStructure(CataclysmStructure structure) {
        structures.add(structure);
    }

    private void startTasks() {
        executorService.scheduleAtFixedRate(this::saveAllMobs, 0, 10, TimeUnit.MINUTES);

        executorService.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::processMobs),
                0, 60, TimeUnit.SECONDS);
    }

    private void saveAllMobs() {
        for (CataclysmStructure structure : structures) {
            if (!isChunkLoaded(structure)) continue;

            try {
                MobLoader.saveAll(structure);
            } catch (Exception e) {
                Cataclysm.getInstance().getLogger().warning("Failed to save mobs for structure: " + e.getMessage());
            }
        }
    }

    private void processMobs() {
        Map<World, List<CataclysmStructure>> structuresByWorld = new HashMap<>();

        for (CataclysmStructure structure : structures) {
            if (!isChunkLoaded(structure)) continue;

            World world = structure.getLevel().getLocation().getWorld();
            structuresByWorld.computeIfAbsent(world, k -> new ArrayList<>()).add(structure);
        }

        for (Map.Entry<World, List<CataclysmStructure>> entry : structuresByWorld.entrySet()) {
            processWorldMobs(entry.getKey(), entry.getValue());
        }
    }

    private void processWorldMobs(World world, List<CataclysmStructure> worldStructures) {
        worldStructures.forEach(s -> s.setMobcap(0));

        Collection<LivingEntity> worldEntities = world.getLivingEntities();

        for (LivingEntity entity : worldEntities) {
            if (entity instanceof Player) continue;

            String mobId = CataclysmMob.getID(entity);
            if (mobId == null || !targetMobIds.contains(mobId.toLowerCase())) continue;

            CataclysmMob.SpawnTag spawnTag = CataclysmMob.getSpawnTag(entity);
            boolean isPersistent = (spawnTag == CataclysmMob.SpawnTag.PERSISTENT);

            for (CataclysmStructure structure : worldStructures) {
                if (isEntityInStructureRange(entity.getLocation(), structure)) {

                    if (!isPersistent) {
                        structure.setMobcap(structure.getMobcap()+1);
                    }

                    if (shouldCleanEntity(entity, isPersistent)) {
                        entity.remove();
                        break;
                    }
                }
            }
        }
    }

    private boolean shouldCleanEntity(LivingEntity entity, boolean isPersistent) {
        if (isPersistent) return false;
        long currentTime = System.currentTimeMillis();
        //Al pasar 90~ segundos, asi nos ahorramos una task funcionando como timer
        if (currentTime % 90000 < 60000) return false;

        Boolean tracked = PersistentData.get(entity, "hasTracked", PersistentDataType.BOOLEAN);
        return !Boolean.TRUE.equals(tracked);
    }

    private boolean isEntityInStructureRange(Location entityLoc, CataclysmStructure structure) {
        Location structureLoc = structure.getLevel().getLocation();
        if (!entityLoc.getWorld().equals(structureLoc.getWorld())) return false;

        double radius = structure.getLevel().radius;
        double radiusSquared = radius * radius;

        double distanceSquared = entityLoc.distanceSquared(structureLoc);
        return distanceSquared <= radiusSquared;
    }

    private boolean isChunkLoaded(CataclysmStructure structure) {
        Location location = structure.getLevel().getLocation();
        return location.getWorld() != null && location.getChunk().isLoaded();
    }

    public boolean isLocationInStructure(Location location) {
        if (location == null) return false;

        for (CataclysmStructure structure : structures) {
            if (!isChunkLoaded(structure)) continue;

            if (isLocationInStructureRange(location, structure)) {
                return true;
            }
        }
        return false;
    }

    public CataclysmStructure getStructureAtLocation(Location location) {
        if (location == null) return null;

        for (CataclysmStructure structure : structures) {
            if (!isChunkLoaded(structure)) continue;

            if (isLocationInStructureRange(location, structure)) {
                return structure;
            }
        }
        return null;
    }

    private boolean isLocationInStructureRange(Location targetLoc, CataclysmStructure structure) {
        Location structureLoc = structure.level.getLocation();
        if (!targetLoc.getWorld().equals(structureLoc.getWorld())) return false;
        double radius = structure.level.radius;
        double radiusSquared = radius * radius;
        double distanceSquared = targetLoc.distanceSquared(structureLoc);

        return distanceSquared <= radiusSquared;
    }

    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
