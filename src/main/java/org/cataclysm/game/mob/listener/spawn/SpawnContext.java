package org.cataclysm.game.mob.listener.spawn;

import net.minecraft.server.level.ServerLevel;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;

import java.util.SplittableRandom;

public class

SpawnContext {

    public LivingEntity entity;
    public final Location location;
    public final int day;
    public final SplittableRandom random;
    public final String biomeName;
    public final ServerLevel level;

    public SpawnContext(LivingEntity entity, Location location, int day, SplittableRandom random) {
        this.entity = entity;
        this.location = location;
        this.day = day;
        this.random = random;
        this.biomeName = location.getBlock().getBiome().translationKey().toUpperCase();
        this.level = ((CraftWorld) location.getWorld()).getHandle();
    }

}
