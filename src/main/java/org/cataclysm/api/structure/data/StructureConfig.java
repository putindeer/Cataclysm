package org.cataclysm.api.structure.data;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.structure.loot.StructureLoot;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StructureConfig {
    private static final @Getter String structuresPath = "dungeons";

    /**
     * The unique identifier for the structure, used to reference its configuration. Always in uppercase.
     */
    public final @Getter String id;

    private final String path;
    private final JsonObject jsonConfig;

    public StructureConfig(@NotNull String id) {
        this.id = id.toUpperCase();
        this.path = structuresPath + "/" + this.id + "/";
        try {
            var file = new File(Cataclysm.getInstance().getDataFolder(), this.path + "config.json");
            this.jsonConfig = JsonParser.parseReader(new FileReader(file)).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Config file not found for structure: " + this.id, e);
        }
    }

    public int getSpawnerCount() {
        if (!this.jsonConfig.has("generation")) return 0;

        var generation = this.jsonConfig.getAsJsonObject("generation");
        if (!generation.has("spawners")) return 0;

        return generation.get("spawners").getAsInt();
    }

    public Vector getArea() {
        if (!this.jsonConfig.has("generation")) return new Vector(0, 0, 0);

        var generation = this.jsonConfig.getAsJsonObject("generation");

        if (!generation.has("area")) return new Vector(0, 0, 0);

        var array = generation.getAsJsonArray("area");

        if (array.size() != 3) throw new IllegalArgumentException("Invalid area vector for structure: " + this.id);

        return new Vector(
                array.get(0).getAsDouble(),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble()
        );
    }

    public Vector getOffSet() {
        if (!this.jsonConfig.has("generation")) return new Vector(0, 0, 0);

        var generation = this.jsonConfig.getAsJsonObject("generation");

        if (!generation.has("offSet")) return new Vector(0, 0, 0);

        var array = generation.getAsJsonArray("offSet");

        if (array.size() != 3) throw new IllegalArgumentException("Invalid offset vector for structure: " + this.id);

        return new Vector(
                array.get(0).getAsDouble(),
                array.get(1).getAsDouble(),
                array.get(2).getAsDouble()
        );
    }

    public StructureLoot getLootTable() {
        var cataclysmLoot = new StructureLoot();

        var lootTables = this.jsonConfig.getAsJsonObject("lootTables");
        if (lootTables == null) throw new NullPointerException("Loot tables not found in config for: " + this.id);

        for (String container : lootTables.keySet()) {
            var containerItems = lootTables.getAsJsonObject(container);
            List<LootHolder> lootHolders = new ArrayList<>();

            for (String item : containerItems.keySet()) {
                var itemData = containerItems.getAsJsonObject(item);
                var min = itemData.has("min") ? itemData.get("min").getAsInt() : 1;
                var max = itemData.has("max") ? itemData.get("max").getAsInt() : 1;
                var rarity = itemData.has("rarity") ? itemData.get("rarity").getAsDouble() : 1.0;

                var itemStack = Material.getMaterial(item) == null
                        ? CataclysmItems.valueOf(item).build()
                        : new ItemStack(Material.valueOf(item));

                lootHolders.add(new LootHolder(itemStack, min, max, rarity));
            }

            var containerMaterial = Material.valueOf(container);
            cataclysmLoot.setLootContainer(containerMaterial, new LootContainer(lootHolders.toArray(new LootHolder[0])));
        }

        return cataclysmLoot;
    }

    public List<CataclysmMob> getPregeneratedMobs() {
        List<CataclysmMob> mobs = new ArrayList<>();
        if (!this.jsonConfig.has("mobs")) throw new IllegalStateException("Missing mobs section in config for: " + this.id);

        for (var mobName : this.jsonConfig.getAsJsonObject("mobs").getAsJsonArray("pregenerated")) {
            mobs.add(CataclysmMob.instantiateMob(mobName.getAsString(), ((CraftWorld) getWorld()).getHandle()));
        }

        return mobs;
    }

    public HashMap<EntityType, CataclysmMob> getSpawnerMobs() {
        if (!this.jsonConfig.has("mobs")) throw new IllegalStateException("Missing mobs section in config for: " + this.id);

        HashMap<EntityType, CataclysmMob> mobs = new HashMap<>();
        for (var mobName : jsonConfig.getAsJsonObject("mobs").getAsJsonArray("spawners")) {
            CataclysmMob mob = CataclysmMob.instantiateMob(mobName.getAsString(), ((CraftWorld) getWorld()).getHandle());
            if (mob != null) mobs.put(mob.getBukkitLivingEntity().getType(), mob);
        }
        return mobs;
    }

    public List<String> getVariants() {
        if (!this.jsonConfig.has("generation")) throw new IllegalStateException("Missing generation section in config: " + this.id);
        var listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(this.jsonConfig.getAsJsonObject("generation").get("variants"), listType);
    }

    public SchematicLoader getSchematicLoader(String variant) {
        return new SchematicLoader(this.path + "schematics/" + variant + ".schem");
    }

    public Material getFillerBlock() {
        if (!this.jsonConfig.has("generation")) return Material.STONE;

        JsonElement element = this.jsonConfig.getAsJsonObject("generation").get("filler");
        if (element == null) return null;

        String name = element.getAsString();
        return Material.valueOf(name.toUpperCase());
    }

    public int getRadius() {
        if (!this.jsonConfig.has("generation")) return 0;
        var radius = this.jsonConfig.getAsJsonObject("generation").get("radius");
        if (radius == null) return 0;
        return radius.getAsInt();
    }

    public World getWorld() {
        if (!this.jsonConfig.has("generation")) return Bukkit.getWorld("world");
        return Bukkit.getWorld(this.jsonConfig.getAsJsonObject("generation").get("world").getAsString());
    }
}