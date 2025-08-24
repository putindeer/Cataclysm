package org.cataclysm.game.mob.utils;

import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.entity.CraftAreaEffectCloud;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class MobUtils {

    /**
     * Verifica si la entidad está sumergida en agua (ya sea agua estática o corriente).
     * Compatible con Minecraft Paper 1.21.5.
     *
     * @param entity La entidad a verificar.
     * @return true si está en agua, false en caso contrario.
     */
    public static boolean isInWater(Entity entity) {
        if (entity == null) return false;

        var loc = entity.getLocation();
        var blockAtFeet = loc.getBlock();

        var type = blockAtFeet.getType();
        return type == Material.WATER || type == Material.BUBBLE_COLUMN;
    }

    public static @Nullable Player getNearestPlayer(@NotNull Location location, int radius) {
        for (Entity entity : location.getNearbyEntities(radius, radius, radius)) {
            if (entity instanceof Player player && player.isValid() && player.getGameMode() == GameMode.SURVIVAL) {
                return player;
            }
        }
        return null;
    }

    public static Location getNearestBlock(Location center, Material material, int radius) {
        Location nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    Location loc = center.clone().add(x, y, z);
                    if (loc.getBlock().getType() == material) {
                        double distance = center.distanceSquared(loc);
                        if (distance < nearestDistance) {
                            nearest = loc;
                            nearestDistance = distance;
                        }
                    }
                }
            }
        }

        return nearest;
    }

    public static boolean isEntityInCloudWithColor(@NotNull LivingEntity entity, double searchRadius, Color targetColor) {
        Location entityLoc = entity.getLocation();

        for (Entity nearby : entity.getNearbyEntities(searchRadius, searchRadius, searchRadius)) {
            if (nearby instanceof AreaEffectCloud cloud) {
                Location cloudLoc = cloud.getLocation();
                double distanceSquared = entityLoc.distanceSquared(cloudLoc);
                double radius = cloud.getRadius();

                if (distanceSquared <= radius * radius) {
                    Color cloudColor = cloud.getColor();
                    if (cloudColor.asRGB() == targetColor.asRGB()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void spawnColoredCloud(float radius, int duration, Color color, @NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return;

        CraftAreaEffectCloud cloud = (CraftAreaEffectCloud) world.spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);

        cloud.setRadius(radius);
        cloud.setDuration(duration);
        cloud.setWaitTime(0);
        cloud.setRadiusOnUse(0);
        cloud.setDurationOnUse(0);
        cloud.setColor(color);
    }

    public static void setGlowingColor(Entity entity, @NotNull NamedTextColor color) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        String teamName = "glow_" + color.toString().toLowerCase();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
            team.color(color);
        }

        team.addEntry(entity.getUniqueId().toString());
        entity.setGlowing(true);
    }

    public static void removeGlowingEffect(@NotNull Entity entity) {
        entity.setGlowing(false);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();

        for (Team team : scoreboard.getTeams()) {
            if (team.hasEntry(entity.getUniqueId().toString())) {
                team.removeEntry(entity.getUniqueId().toString());

                if (team.getEntries().isEmpty()) {
                    team.unregister();
                }
            }
        }
    }

    public static boolean isInColdBiome(@NotNull Entity entity) {
        Location location = entity.getLocation();
        String biomeName = location.getBlock().getBiome().translationKey().toUpperCase();
        return biomeName.contains("SNOWY") || biomeName.contains("FROZEN") || biomeName.contains("ICE") || biomeName.contains("JAGGED") || biomeName.contains("TAIGA") || biomeName.equals("GROVE");
    }

    public static boolean isInWarmBiome(@NotNull Entity entity) {
        Location location = entity.getLocation();
        String biomeName = location.getBlock().getBiome().translationKey().toUpperCase();
        return biomeName.contains("WARM") || biomeName.contains("DESERT") || biomeName.contains("JUNGLE") || biomeName.contains("BADLANDS") || biomeName.contains("SAVANNA");
    }

    public static void healthBoost(LivingEntity entity, double amplifier) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.MAX_HEALTH);
        if (maxHealth != null) {
            maxHealth.setBaseValue(maxHealth.getBaseValue() + (4 * amplifier));
            entity.setHealth(maxHealth.getValue());
        }
    }

    public static void scaleBoost(LivingEntity entity, double amplifier) {
        var scale = entity.getAttribute(Attribute.SCALE);
        if (scale != null) scale.setBaseValue(scale.getBaseValue() * amplifier);
    }

    public static void damageBoost(LivingEntity entity, double amplifier) {
        AttributeInstance attackDamage = entity.getAttribute(Attribute.ATTACK_DAMAGE);
        if (attackDamage != null) attackDamage.setBaseValue(attackDamage.getBaseValue() + (3 * amplifier));
    }

    public static void speedBoost(LivingEntity entity, double amplifier) {
        AttributeInstance movementSpeed = entity.getAttribute(Attribute.MOVEMENT_SPEED);
        if (movementSpeed != null)
            movementSpeed.setBaseValue(movementSpeed.getBaseValue() + (movementSpeed.getBaseValue() * ((20 * amplifier) / 100)));
    }

    public static void letalBoost(LivingEntity entity, double amplifier) {
        healthBoost(entity, amplifier);
        damageBoost(entity, amplifier);
        speedBoost(entity, amplifier);
    }

    public static void multiplyAttribute(LivingEntity entity, Attribute attribute, double amplifier) {
        AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance != null) {
            attributeInstance.setBaseValue(attributeInstance.getBaseValue() * amplifier);
            if (attribute == Attribute.MAX_HEALTH) entity.setHealth(attributeInstance.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerAttribute(net.minecraft.world.entity.LivingEntity entity, Holder<net.minecraft.world.entity.ai.attributes.Attribute> attribute, double value) {
        try {
            var attributeField = AttributeMap.class.getDeclaredField("b");
            attributeField.setAccessible(true);

            net.minecraft.world.entity.ai.attributes.AttributeInstance attributeModifiable = new net.minecraft.world.entity.ai.attributes.AttributeInstance(attribute, net.minecraft.world.entity.ai.attributes.AttributeInstance::getAttribute);

            try {
                var map = (Map<Holder<net.minecraft.world.entity.ai.attributes.Attribute>, net.minecraft.world.entity.ai.attributes.AttributeInstance>) attributeField.get(entity.getAttributes());
                map.put(attribute, attributeModifiable);
                attributeField.set(entity.getAttributes(), map);
            } catch (ClassCastException ignored) {
            }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException ignored) {
        }

        net.minecraft.world.entity.ai.attributes.AttributeInstance attributeInstance = entity.getAttribute(attribute);
        if (attributeInstance != null) attributeInstance.setBaseValue(value);
    }

}
