package org.cataclysm.api.boss;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record CataclysmArea(Location center, int radius) {

    public boolean isLocationInArea(@NotNull Location location) {
        if (location.getWorld() != this.center.getWorld()) return false;

        double distanceSquared = this.center.distanceSquared(location);
        return distanceSquared <= (this.radius * this.radius);
    }

    public boolean isLivingEntityInArea(LivingEntity livingEntity) {
        List<LivingEntity> livingEntities = this.getLivingEntitiesInArena();
        return livingEntities.contains(livingEntity);
    }

    public List<LivingEntity> getLivingEntitiesInArena() {
        return center.getWorld().getNearbyLivingEntities(this.center, this.radius, this.radius, this.radius).stream()
                .toList();
    }

    public List<Player> getPlayersInArena() {
        return center.getWorld().getNearbyEntities(this.center, this.radius, this.radius, this.radius).stream()
                .filter(entity -> entity instanceof org.bukkit.entity.Player)
                .map(entity -> (org.bukkit.entity.Player) entity)
                .toList();
    }

    public @NotNull List<Location> getRandomLocations(int amount) {
        var locations = new ArrayList<Location>();
        var world = this.center.getWorld();
        var random = ThreadLocalRandom.current();

        int maxAttempts = amount * 20; // Límite para evitar bucles infinitos
        int attempts = 0;

        while (locations.size() < amount && attempts < maxAttempts) {
            attempts++;

            double theta = random.nextDouble() * 2 * Math.PI;
            double r = this.radius * Math.sqrt(random.nextDouble());

            double xOffset = r * Math.cos(theta);
            double zOffset = r * Math.sin(theta);

            double x = this.center.getBlockX() + Math.round(xOffset);
            double z = this.center.getBlockZ() + Math.round(zOffset);
            double y = this.center.getY();

            Location candidate = new Location(world, x + 0.5, y, z + 0.5);

            boolean valid = true;
            for (Location loc : locations) {
                if (loc.distance(candidate) < 10) {
                    valid = false;
                    break;
                }
            }

            if (valid) {
                locations.add(candidate);
            }
        }

        return locations;
    }

    /**
     * Devuelve todas las ubicaciones dentro del área donde exista un bloque específico.
     *
     * @param material El tipo de bloque a buscar.
     * @return Lista de ubicaciones con el bloque especificado.
     */
    public @NotNull List<Location> getBlockLocations(@NotNull Material material) {
        var locations = new ArrayList<Location>();
        var world = this.center.getWorld();

        int minX = this.center.getBlockX() - this.radius;
        int maxX = this.center.getBlockX() + this.radius;
        int minY = Math.max(world.getMinHeight(), this.center.getBlockY() - this.radius);
        int maxY = Math.min(world.getMaxHeight(), this.center.getBlockY() + this.radius);
        int minZ = this.center.getBlockZ() - this.radius;
        int maxZ = this.center.getBlockZ() + this.radius;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == material) {
                        locations.add(block.getLocation());
                    }
                }
            }
        }

        return locations;
    }

}