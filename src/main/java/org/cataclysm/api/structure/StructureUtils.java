package org.cataclysm.api.structure;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class StructureUtils {

    /**
     * Busca todos los Spawners en un radio dado desde un punto central.
     *
     * @param center Centro de la búsqueda.
     * @param radius Radio de búsqueda (en bloques).
     * @return Lista de CreatureSpawner encontrados.
     */
    public static Collection<CreatureSpawner> getSpawnersInArea(Location center, int radius) {
        var spawners = new ArrayList<CreatureSpawner>();

        int minX = center.getBlockX() - radius;
        int maxX = center.getBlockX() + radius;
        int minY = Math.max(0, center.getBlockY() - radius);
        int maxY = Math.min(center.getWorld().getMaxHeight(), center.getBlockY() + radius);
        int minZ = center.getBlockZ() - radius;
        int maxZ = center.getBlockZ() + radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    var block = center.getWorld().getBlockAt(x, y, z);
                    if (block.getType() == Material.SPAWNER) {
                        if (block.getState() instanceof CreatureSpawner spawner) {
                            spawners.add(spawner);
                        }
                    }
                }
            }
        }

        return spawners;
    }

    public static boolean hasPlayerVisitVariants(@NotNull CataclysmStructure structure, Player player) {
        for (var variant : structure.config.getVariants()) {
            if (!hasPlayerVisitVariant(variant, player)) return false;
        }
        return true;
    }

    public static void setPlayerVisitVariant(String variant, Player player, boolean visited) {
        PersistentData.set(player, variant, PersistentDataType.BOOLEAN, visited);
    }

    public static boolean hasPlayerVisitVariant(String string, Player player) {
        var data = PersistentData.get(player, string, PersistentDataType.BOOLEAN);
        if (data == null) return false;
        return data;
    }

    public static @Nullable CataclysmMob getMobByMaterial(Material material, CataclysmStructure structure) {
        if (structure == null) return null;

        var pregeneratedMobs = structure.config.getPregeneratedMobs();

        if (pregeneratedMobs.isEmpty()) return null;

        for (CataclysmMob mob : pregeneratedMobs) {
            if (mob.getPregenerationMaterial() == material) return mob;
        }
        return null;
    }

    public static boolean isLocationInStructure(@NotNull Location location, @NotNull CataclysmStructure structure) {
        Location structureLoc = structure.level.getLocation();
        if (!location.getWorld().equals(structureLoc.getWorld())) return false;

        int dx = Math.abs(location.getBlockX() - structureLoc.getBlockX());
        int dy = Math.abs(location.getBlockY() - structureLoc.getBlockY());
        int dz = Math.abs(location.getBlockZ() - structureLoc.getBlockZ());

        int radius = structure.level.radius;
        int xOffSet = radius;
        int yOffSet = radius;
        int zOffSet = radius;

        var area = structure.config.getArea();
        if (!area.isZero()) {
            xOffSet = area.getBlockX();
            yOffSet = area.getBlockY();
            zOffSet = area.getBlockZ();
        }

        return dx <= xOffSet && dy <= yOffSet && dz <= zOffSet;
    }

    public static boolean isEntityInStructure(@NotNull Entity entity, @NotNull CataclysmStructure structure) {
        return isLocationInStructure(entity.getLocation(), structure);
    }
}