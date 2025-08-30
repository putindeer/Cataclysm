package org.cataclysm.game.mob.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Random;

public class TeleportUtils {

    private static final Random random = new Random();
    private static final int MAX_ATTEMPTS = 100;

    public static void teleportEntityNearPlayer(LivingEntity livingEntity, double searchRadius, double minDistance, double maxDistance) {
        Location targetLocation = getNearestRandomPlayerLocation(livingEntity, searchRadius, minDistance, maxDistance);
        if (targetLocation != null) {
            var world = livingEntity.getWorld();
            world.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 4f, 0.78f);
            world.playSound(targetLocation, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 4f, 0.88f);
            livingEntity.teleport(targetLocation);
            livingEntity.setNoDamageTicks(5);
        }
    }

    public static Location getNearestRandomPlayerLocation(LivingEntity livingEntity, double searchRadius, double minDistance, double maxDistance) {
        if (livingEntity == null || livingEntity.isDead()) return null;

        if (minDistance < 0 || maxDistance <= minDistance) {
            throw new IllegalArgumentException("Invalid distance parameters: minDistance must be >= 0 and maxDistance must be > minDistance");
        }

        Location currentLoc = livingEntity.getLocation();
        World world = currentLoc.getWorld();

        if (world == null) return null;

        Player nearestPlayer = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Player player : world.getPlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) continue;

            double distance = player.getLocation().distance(currentLoc);
            if (distance <= searchRadius && distance < nearestDistance) {
                nearestPlayer = player;
                nearestDistance = distance;
            }
        }

        if (nearestPlayer == null) return null;

        Location playerLoc = nearestPlayer.getLocation();
        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = minDistance + (random.nextDouble() * (maxDistance - minDistance));
            double newX = playerLoc.getX() + (Math.cos(angle) * distance);
            double newZ = playerLoc.getZ() + (Math.sin(angle) * distance);

            double baseY = playerLoc.getY();
            double yOffset = (random.nextDouble() * (maxDistance - minDistance) * 0.5) - ((maxDistance - minDistance) * 0.25);
            double newY = baseY + yOffset;
            newY = Math.max(world.getMinHeight() + 4, Math.min(world.getMaxHeight() - 4, newY));

            Location targetLocation = new Location(world, newX, newY, newZ);
            if (isLocationSafe(livingEntity, targetLocation)) return targetLocation;
        }

        return null;
    }

    /**
     * Teleports an Entity to a random location within the specified radius
     * @param livingEntity The LivingEntity to teleport
     * @param radius The radius in blocks from the LivingEntity's current position
     * @return true if teleportation was successful, false if no valid location was found
     */
    public static boolean teleportEntityRandomly(LivingEntity livingEntity, double radius) {
        if (livingEntity == null || livingEntity.isDead()) {
            return false;
        }

        Location currentLoc = livingEntity.getLocation();
        World world = currentLoc.getWorld();

        if (world == null) {
            return false;
        }

        for (int attempts = 0; attempts < MAX_ATTEMPTS; attempts++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = random.nextDouble() * radius;
            double newX = currentLoc.getX() + (Math.cos(angle) * distance);
            double newZ = currentLoc.getZ() + (Math.sin(angle) * distance);
            double newY = currentLoc.getY() + (random.nextDouble() * radius * 0.5) - (radius * 0.25);
            newY = Math.max(world.getMinHeight() + 4, Math.min(world.getMaxHeight() - 4, newY));

            Location targetLocation = new Location(world, newX, newY, newZ);

            if (isLocationSafe(livingEntity, targetLocation)) {
                livingEntity.teleport(targetLocation);
                livingEntity.setNoDamageTicks(5);
                world.playSound(targetLocation, Sound.ENTITY_ENDERMAN_TELEPORT, 4f, 0.78f);
                world.playSound(targetLocation, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 4f, 0.88f);
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if a location is safe for the Entity (won't cause suffocation)
     * @param livingEntity The entity to check for
     * @param location The location to check
     * @return true if the location is safe, false otherwise
     */
    public static boolean isLocationSafe(LivingEntity livingEntity, Location location) {
        World world = location.getWorld();
        if (world == null) return false;

        BoundingBox boundingBox = livingEntity.getBoundingBox();
        double width = boundingBox.getWidthX();
        double height = boundingBox.getHeight();
        double depth = boundingBox.getWidthZ();

        int minX = (int) Math.floor(location.getX() - width / 2);
        int maxX = (int) Math.ceil(location.getX() + width / 2);
        int minY = (int) Math.floor(location.getY());
        int maxY = (int) Math.ceil(location.getY() + height);
        int minZ = (int) Math.floor(location.getZ() - depth / 2);
        int maxZ = (int) Math.ceil(location.getZ() + depth / 2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Material blockType = world.getBlockAt(x, y, z).getType();
                    if (blockType.isSolid()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static Location findClosestVerticalBlock(Player player) {
        var world = player.getWorld();
        var playerLoc = player.getLocation();
        int x = playerLoc.getBlockX();
        int z = playerLoc.getBlockZ();
        int playerY = playerLoc.getBlockY();
        int maxY = world.getMaxHeight() - 1;

        for (int y = playerY + 2; y <= maxY; y++) {
            Block block = world.getBlockAt(x, y, z);
            if (block.getType() == Material.GOLD_BLOCK) {
                return new Location(world, x, y, z);
            }
        }

        return null;
    }
}
