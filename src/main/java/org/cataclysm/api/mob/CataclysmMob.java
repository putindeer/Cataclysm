package org.cataclysm.api.mob;

import com.fastasyncworldedit.bukkit.util.BukkitItemStack;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.data.json.JsonConfig;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.cataclysm.global.utils.serializer.Base64Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class CataclysmMob implements Serializable {
    private static final String TOKEN_DATA_KEY = "TOKEN";
    private static final String FAMILY_DATA_KEY = "FAMILY";
    private static final String ID_DATA_KEY = "CUSTOM";
    private static final String SPAWN_TAG_DATA_KEY = "SPAWN_TAG";
    private static final String SPAWNER_DATA_KEY = "SPAWNER_UUID";

    private static final String PREGENERATION_MATERIAL_DATA_KEY = "PREGENERATION_MATERIAL";
    private static final String DUNGEON_DATA_KEY = "DUNGEON";

    private @Getter final MobName mobName;
    private final String worldName;
    private final String tokenKey;

    private transient @Getter Level level;
    private transient @Getter LivingEntity entity;
    private transient @Getter CraftLivingEntity bukkitLivingEntity;
    private transient @Getter @Setter @Nullable CataclysmDrops drops;
    private transient @Getter @Setter @Nullable Listener listener;
    private transient @Getter CataclysmToken mobToken;
    private transient @Getter SpawnTag spawnTag;

    public CataclysmMob(@NotNull LivingEntity livingEntity, String name, Level level) {
        this(livingEntity, new MobName(name, CataclysmColor.VANILLA.getColor()), level);
    }

    public CataclysmMob(@NotNull LivingEntity livingEntity, String name, @NotNull CataclysmColor color, Level level) {
        this(livingEntity, new MobName(name, color.getColor()), level);
    }

    public CataclysmMob(@NotNull LivingEntity livingEntity, String name, String color, Level level) {
        this(livingEntity, new MobName(name, color), level);
    }

    public CataclysmMob(@NotNull LivingEntity livingEntity, @NotNull MobName name, Level level) {
        this.mobName = name;
        this.level = level;
        this.entity = livingEntity;
        this.bukkitLivingEntity = livingEntity.getBukkitLivingEntity();
        this.worldName = livingEntity.getBukkitLivingEntity().getWorld().getName();
        this.tokenKey = CataclysmToken.generate().key();
        this.setName(mobName.display(), mobName.color());
        this.setID(asId(mobName.display()));
    }

    public void setName(String name, String color) {
        this.entity.setCustomName(Component.literal(name).withStyle(Style.EMPTY.withColor(TextColor.parseColor(color).getOrThrow())));
    }

    public void setItem(EquipmentSlot slot, ItemStack itemStack) {
        this.entity.setItemSlot(slot, itemStack);
    }

    public void setItem(EquipmentSlot slot, ItemLike itemLike) {
        this.setItem(slot, new ItemStack(itemLike));
    }

    public void setAttribute(Holder<Attribute> attribute, double baseValue) {
        AttributeInstance attr = this.entity.getAttribute(attribute);
        if (attr != null) attr.setBaseValue(baseValue);
    }

    public void amplifyAttribute(Holder<Attribute> attribute, double amplifier) {
        this.setAttribute(attribute, this.getEntity().getAttributeBaseValue(attribute) * amplifier);
    }

    public void setHealth(int health) {
        this.setAttribute(Attributes.MAX_HEALTH, health);
        this.entity.setHealth(health);
    }

    public void setCollidable(boolean collidable) {
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = sb.getTeam("nocollision");

        if (team == null) team = sb.registerNewTeam("nocollision");

        Team.OptionStatus status = Team.OptionStatus.NEVER;
        if (collidable) status = Team.OptionStatus.ALWAYS;

        team.setOption(Team.Option.COLLISION_RULE, status);
        team.addEntity(this.getBukkitLivingEntity());
    }

    protected void setPersistentDataString(@NotNull String key, @NotNull String value) {
        PersistentData.set(this.bukkitLivingEntity, key, PersistentDataType.STRING, value);
    }

    protected String getPersistentDataString(String key) {
        return PersistentData.get(this.bukkitLivingEntity, key, PersistentDataType.STRING);
    }

    public void setID(String id) {
        this.setPersistentDataString(ID_DATA_KEY, id);
    }

    public @Nullable String getID() {
        return getID(this.getBukkitLivingEntity());
    }

    public static @Nullable String getID(@NotNull org.bukkit.entity.LivingEntity livingEntity) {
        return PersistentData.get(livingEntity, ID_DATA_KEY, PersistentDataType.STRING);
    }

    public static @NotNull String asId(@NotNull String display) {
        return display.replace(" ", "");
    }

    public void setFamily(String familyId) {
        this.setPersistentDataString(FAMILY_DATA_KEY, familyId);
    }

    public static @Nullable String getFamily(@NotNull LivingEntity livingEntity) {
        return getFamily(livingEntity.getBukkitLivingEntity());
    }

    public static @Nullable String getFamily(@NotNull org.bukkit.entity.LivingEntity livingEntity) {
        return PersistentData.get(livingEntity, FAMILY_DATA_KEY, PersistentDataType.STRING);
    }

    private void setToken(@NotNull CataclysmToken token) {
        this.mobToken = token;
        this.setPersistentDataString(TOKEN_DATA_KEY, token.key());
    }

    public static @Nullable CataclysmToken getToken(org.bukkit.entity.LivingEntity livingEntity) {
        return getToken(((CraftLivingEntity) livingEntity).getHandle());
    }

    public static @Nullable CataclysmToken getToken(@NotNull LivingEntity livingEntity) {
        String token = PersistentData.get(livingEntity.getBukkitLivingEntity(), TOKEN_DATA_KEY, PersistentDataType.STRING);
        if (token != null) return new CataclysmToken(token);
        else return null;
    }

    public void setSpawnTag(@NotNull SpawnTag spawnTag) {
        this.spawnTag = spawnTag;
        this.setPersistentDataString(SPAWN_TAG_DATA_KEY, spawnTag.name());
    }

    public void setStructure(@NotNull CataclysmStructure structure) {
        this.setPersistentDataString(DUNGEON_DATA_KEY, structure.getUuid().toString());
    }

    /**
     * Gets the Cataclysm Structure of a Cataclysm Mob. Important: This function DOES NOT work with instantiated mobs.
     */
    public @Nullable CataclysmStructure getStructure() {
        String uuidString = this.getPersistentDataString(DUNGEON_DATA_KEY);
        return uuidString != null ? CataclysmStructure.getStructures().get(UUID.fromString(uuidString)) : null;
    }

    public static @Nullable CataclysmStructure getStructure(org.bukkit.entity.LivingEntity entity) {
        String uuidString = PersistentData.get(entity, DUNGEON_DATA_KEY, PersistentDataType.STRING);
        return uuidString != null ? CataclysmStructure.getStructures().get(UUID.fromString(uuidString)) : null;
    }

    public void setPregenerationMaterial(@NotNull Material material) {
        this.setPersistentDataString(PREGENERATION_MATERIAL_DATA_KEY, material.name());
    }

    public @Nullable Material getPregenerationMaterial() {
        String materialName = PersistentData.get(this.bukkitLivingEntity, PREGENERATION_MATERIAL_DATA_KEY, PersistentDataType.STRING);
        return materialName != null ? Material.getMaterial(materialName) : null;
    }

    public static void setSpawnTag(org.bukkit.entity.LivingEntity livingEntity, @NotNull SpawnTag spawnTag) {
        PersistentData.set(livingEntity, SPAWN_TAG_DATA_KEY, PersistentDataType.STRING, spawnTag.name());
    }

    public static SpawnTag getSpawnTag(@NotNull org.bukkit.entity.LivingEntity livingEntity) {
        return getSpawnTag(((CraftLivingEntity) livingEntity).getHandle());
    }

    public static SpawnTag getSpawnTag(@NotNull LivingEntity livingEntity) {
        String spawnTag = PersistentData.get(livingEntity.getBukkitLivingEntity(), SPAWN_TAG_DATA_KEY, PersistentDataType.STRING);
        if (spawnTag != null) return SpawnTag.valueOf(spawnTag);
        else return SpawnTag.EMPTY;
    }

    public void setLeatherArmor(Color color) {
        this.setItem(EquipmentSlot.HEAD, createDyedLeatherArmor(Material.LEATHER_HELMET, color));
        this.setItem(EquipmentSlot.CHEST, createDyedLeatherArmor(Material.LEATHER_CHESTPLATE, color));
        this.setItem(EquipmentSlot.LEGS, createDyedLeatherArmor(Material.LEATHER_LEGGINGS, color));
        this.setItem(EquipmentSlot.FEET, createDyedLeatherArmor(Material.LEATHER_BOOTS, color));
    }

    private net.minecraft.world.item.ItemStack createDyedLeatherArmor(Material material, Color color) {
        BukkitItemStack item = new BukkitItemStack(new org.bukkit.inventory.ItemStack(material));
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getBukkitItemStack().getItemMeta();
        if (meta != null) {
            meta.setColor(color);
            item.getBukkitItemStack().setItemMeta(meta);
        }
        return CraftItemStack.asNMSCopy(item.getBukkitItemStack());
    }

    public void addFreshEntity(Location location) {
        this.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.CUSTOM);
        this.getBukkitLivingEntity().addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
    }

    public void addFreshEntity(Location location, CreatureSpawnEvent.SpawnReason spawnReason) {
        this.bukkitLivingEntity.teleport(location);
        this.setToken(new CataclysmToken(this.tokenKey));
        if (this.listener != null) Bukkit.getPluginManager().registerEvents(this.listener, Cataclysm.getInstance());
        var store = Cataclysm.getStore();
        if (this.getStructure() != null) {
            store = this.getStructure().getMobStore();
            try {
                new MobLoader(this, this.getStructure()).save();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(this.entity, spawnReason);

        store.getStorer().store(this);
    }

    public void cloneMob(@NotNull Location location) {
        CataclysmMob clone = this.createInstance();

        if (clone == null) {
            throw new IllegalStateException("Clone instance cannot be null. Ensure createInstance() is implemented correctly. Clone: " + this.getClass().getName());
        }

        var id = CataclysmMob.getID(this.getBukkitLivingEntity());

        for (AttributeInstance attr : this.getEntity().getAttributes().getSyncableAttributes()) {
            if (id != null && id.equalsIgnoreCase("arcanesculpture")) {
                continue;
            }
            Holder<Attribute> holder = attr.getAttribute();
            clone.setAttribute(holder, attr.getBaseValue());
        }

        clone.setHealth((int) this.getEntity().getHealth());
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (id != null && id.equalsIgnoreCase("arcanesculpture")) continue;
            ItemStack item = this.getEntity().getItemBySlot(slot);
            if (!item.isEmpty()) clone.setItem(slot, item.copy());
        }

        if (this.getStructure() != null) clone.setStructure(this.getStructure());

        clone.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.COMMAND);

        var structure = clone.getStructure();
        if (structure == null) return;

        try {
            new MobLoader(this, structure).save();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Run this once during plugin enable
    private static final Map<String, Constructor<? extends CataclysmMob>> MOB_CONSTRUCTORS = new HashMap<>();

    public static void initializeMobConstructors() {
        Reflections reflections = new Reflections("org.cataclysm.game.mob.custom");
        for (Class<? extends CataclysmMob> clazz : reflections.getSubTypesOf(CataclysmMob.class)) {
            try {
                Constructor<? extends CataclysmMob> constructor = clazz.getConstructor(Level.class);
                MOB_CONSTRUCTORS.put(clazz.getSimpleName(), constructor);
            } catch (NoSuchMethodException ignored) {
                Bukkit.getLogger().warning("Mob class " + clazz.getName() + " is missing the Level constructor!");
            }
        }
    }

    public static @Nullable CataclysmMob instantiateMob(String mobName, Level level) {
        Constructor<? extends CataclysmMob> constructor = MOB_CONSTRUCTORS.get(mobName);
        if (constructor == null) {
            return null;
        }
        try {
            return constructor.newInstance(level);
        } catch (Exception e) {
            throw new RuntimeException("Could not instantiate mob: " + mobName, e);
        }
    }

    protected abstract CataclysmMob createInstance();

    public void save(@NotNull JsonConfig config) throws Exception {
        String base64 = Base64Utils.encodeInstanceToBase64(this);
        JsonObject obj = new JsonObject();
        obj.addProperty("data", base64);
        config.setJsonObject(obj);
        config.save();
    }

    public void restore(LivingEntity livingEntity) {
        World bukkitWorld = Bukkit.getWorld(this.worldName);
        if (bukkitWorld == null) throw new IllegalStateException("World not found: " + this.worldName);

        this.level = ((CraftWorld) bukkitWorld).getHandle();
        this.entity = livingEntity;
        this.bukkitLivingEntity = livingEntity.getBukkitLivingEntity();
    }

    public void updateToken() {
        this.mobToken = new CataclysmToken(this.tokenKey);
    }

    /**
     * Etiqueta de persistencia para el spawn de un mob.
     */
    public enum SpawnTag {
        /**
         * El mob será persistente y no se eliminará automáticamente.
         */
        PERSISTENT,
        /**
         * No se agrega un spawn tag al mob.
         */
        EMPTY
    }

    public record MobName(String display, String color) implements Serializable {
    }
}