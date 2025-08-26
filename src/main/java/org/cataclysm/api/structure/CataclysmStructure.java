package org.cataclysm.api.structure;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.MobLoader;
import org.cataclysm.api.mob.store.MobStore;
import org.cataclysm.api.structure.data.StructureConfig;
import org.cataclysm.api.structure.data.StructureLevel;
import org.cataclysm.api.structure.data.StructureLoader;
import org.cataclysm.game.world.dungeons.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public abstract class CataclysmStructure {
    private static final @Getter HashMap<UUID, CataclysmStructure> structures = new HashMap<>();
    public boolean ignoreAirBlocks = true;

    protected final @Getter UUID uuid;
    protected final @Getter StructureConfig config;
    private final @Getter MobStore mobStore = new MobStore();
    protected @Getter @Setter int mobcap;
    protected @Getter StructureLevel level;
    protected Listener listener;

    //Usado para restaurar una Dungeon desde un JsonConfig.
    public CataclysmStructure(@NotNull StructureLevel level) {
        this.uuid = level.uuid;
        this.config = new StructureConfig(level.id);
        this.level = level;
    }

    //Usado para generar una Dungeon desde cero.
    public CataclysmStructure(@NotNull String id) {
        this.uuid = UUID.randomUUID();
        this.config = new StructureConfig(id.toUpperCase());
    }

    public void generate(@NotNull Location location, String variant) {
        this.level = new StructureLevel(this.uuid, this.config.id, variant, location.clone(), this.config.getRadius());
        this.config.getSchematicLoader(variant).pasteSchematic(location.add(this.config.getOffSet()), this.ignoreAirBlocks);

        location.getBlock().setType(Material.LODESTONE);

        this.setUp();
        this.updateBlockSet();
    }

    public void setUp() {
        try {
            new StructureLoader(this.config.getId(), this.uuid).save(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save structure: " + this.config.getId(), e);
        }

        structures.put(this.uuid, this);

        //SpawnerLoader.loadAll(this);
        MobLoader.loadAll(this);
        StructureManager.getInstance().registerStructure(this);

        Bukkit.getPluginManager().registerEvents(new StructureListener(this), Cataclysm.getInstance());

        if (this.listener == null) return;
        Bukkit.getPluginManager().registerEvents(this.listener, Cataclysm.getInstance());
    }

    protected void updateBlockSet() {
        var structureLoot = this.config.getLootTable();
        ArrayList<Block> blocksToUpdate = new ArrayList<>();
        var location = this.level.getLocation();
        var cx = location.getBlockX();
        var cy = location.getBlockY();
        var cz = location.getBlockZ();

        var radius = this.config.getRadius();
        int xOffSet = radius;
        int yOffSet = radius;
        int zOffSet = radius;

        var area = this.config.getArea();
        if (!area.isZero()) {
            xOffSet = area.getBlockX();
            yOffSet = area.getBlockY();
            zOffSet = area.getBlockZ();
        }

        var pregeneratedMobs = this.config.getPregeneratedMobs();
        var mobMaterials = pregeneratedMobs.stream()
                .map(CataclysmMob::getPregenerationMaterial)
                .toList();

        for (int x = cx - xOffSet; x <= cx + xOffSet; x++) {
            for (int y = cy - yOffSet; y <= cy + yOffSet; y++) {
                for (int z = cz - zOffSet; z <= cz + zOffSet; z++) {
                    var block = location.getWorld().getBlockAt(x, y, z);
                    var type = block.getType();
                    if (type.isAir()) continue;

                    if (type.equals(Material.CREAKING_HEART)) {
                        blocksToUpdate.add(block);
                    }

                    if (type.equals(this.config.getFillerBlock())) block.setType(Material.AIR);

                    if (mobMaterials.contains(type)) {
                        block.setType(Material.AIR);

                        CataclysmMob mob = StructureUtils.getMobByMaterial(type, this);

                        if (mob == null) continue;

                        mob.setStructure(this);
                        mob.addFreshEntity(block.getLocation().add(0.5, 0, 0.5), CreatureSpawnEvent.SpawnReason.COMMAND);
                    }

                    var state = block.getState();
                    if (state instanceof BlockInventoryHolder inventoryHolder && inventoryHolder.getInventory().isEmpty()) structureLoot.apply(inventoryHolder);
                }
            }
        }

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            blocksToUpdate.forEach(block -> block.getState().update());
            Bukkit.getConsoleSender().sendMessage("Updated " + blocksToUpdate.size() + " blocks");
        }, 20L);
    }

    /**
     * Returns the path where the structure data is stored.
     * The path is constructed using the structure's ID and UUID.
     *
     * @return The path as a String. Example: "dungeons/PARAGON_TEMPLE/structures/f257353a-ab39-4cee-bffb-97637f038752"
     */
    public String getPath() {
        return StructureConfig.getStructuresPath() + "/" + this.config.getId() + "/structures/" + this.uuid.toString();
    }

    public void save(@NotNull JsonConfig jsonConfig) {
        jsonConfig.setJsonObject(Cataclysm.getGson().toJsonTree(this.level).getAsJsonObject());
        try {
            jsonConfig.save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @NotNull CataclysmStructure restore(@NotNull JsonConfig jsonConfig) {
        var level = Cataclysm.getGson().fromJson(jsonConfig.getJsonObject(), StructureLevel.class);
        return switch (level.id) {
            case "PARAGON_TEMPLE" -> new ParagonTemple(level);
            case "MONOLITH" -> new Monolith(level);
            case "CALAMITY_CHAMBER" -> new CalamityChamber(level);
            case "MIRAGE_CITADEL" -> new MirageCitadel(level);
            case "PALE_TEMPLE" -> new PaleTemple(level);
            default -> throw new IllegalStateException("Unexpected value: " + level.id);
        };
    }

    public static @Nullable CataclysmStructure getNearestStructure(World world, String id) {
        CataclysmStructure nearestStructure = null;

        for (var entry : CataclysmStructure.getStructures().entrySet()) {
            var candidate = entry.getValue();

            if (!candidate.getConfig().getId().equalsIgnoreCase(id)) continue;

            if (nearestStructure == null) nearestStructure = candidate;
            else {
                var location = world.getSpawnLocation();
                var candidateLocation = candidate.getLevel().getLocation();
                var currentLocation = nearestStructure.getLevel().getLocation();
                if (candidateLocation.distance(location) < currentLocation.distance(location)) nearestStructure = candidate;
            }
        }

        return nearestStructure;
    }

    public boolean isLooted() {
        var spawners = StructureUtils.getSpawnersInArea(this.level.getLocation(), this.level.radius);
        var count = spawners.size();
        var initCount = this.config.getSpawnerCount();
        return (initCount - 20) > count;
    }

    public void duplicate() {
        CataclysmStructure structure;
        switch (this.level.id) {
            case "PARAGON_TEMPLE" -> structure = new ParagonTemple(this.level);
            case "MONOLITH" -> structure = new Monolith(this.level);
            case "CALAMITY_CHAMBER" -> structure = new CalamityChamber(this.level);
            case "MIRAGE_CITADEL" -> structure = new MirageCitadel(this.level);
            case "PALE_TEMPLE" -> structure = new PaleTemple(this.level);
            default -> structure = null;
        }

        if (structure == null) Bukkit.getConsoleSender().sendMessage("Failed to restore structure: " + this.level.id);
        else structure.generate(this.level.getLocation(), this.level.variant);
    }

    public void delete() {
        try {
            var loader = new StructureLoader(this.config.getId(), this.uuid);
            var folder =  loader.jsonConfig.getFile().getParentFile();

            var success = folder.delete();
            if (success) {
                Bukkit.getConsoleSender().sendMessage("Structure " + this.config.getId() + " deleted successfully.");
                CataclysmStructure.getStructures().remove(this.uuid);
            } else {
                Bukkit.getConsoleSender().sendMessage("Failed to delete structure " + this.config.getId() + ".");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getAdvancement();
}