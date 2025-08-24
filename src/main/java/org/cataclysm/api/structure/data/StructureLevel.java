package org.cataclysm.api.structure.data;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class StructureLevel {
    public final UUID uuid;
    public final @Getter String variant;
    public final String id;
    public final int radius;
    private final String worldName;
    private final int blockX, blockY, blockZ;

    protected @Getter int initialSpawners;

    public StructureLevel(UUID uuid, String id, String variant, @NotNull Location location, int radius) {
        this.uuid = uuid;
        this.variant = variant;
        this.id = id;
        this.worldName = location.getWorld().getName();
        this.blockX = location.getBlockX();
        this.blockY = location.getBlockY();
        this.blockZ = location.getBlockZ();
        this.radius = radius;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName), this.blockX, this.blockY, this.blockZ);
    }
}