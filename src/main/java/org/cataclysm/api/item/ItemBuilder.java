package org.cataclysm.api.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.serialization.Codec;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.tag.DamageTypeTags;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.items.ItemFamily;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ItemBuilder implements Cloneable {
    public ItemMeta meta;
    public final ItemStack item;

    public ItemBuilder(String base64) {this.item = this.createCustomHead(base64);}
    public ItemBuilder(ItemStack item) {this.item = item;}
    public ItemBuilder(Material material) {this.item = new ItemStack(material);}

    public ItemBuilder setDisplay(String display) {
        this.meta = this.item.getItemMeta();
        this.meta.displayName(MiniMessage.miniMessage().deserialize(display).decoration(TextDecoration.ITALIC, false));
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setDisplay(Component display) {
        this.meta = this.item.getItemMeta();
        this.meta.displayName(display.decoration(TextDecoration.ITALIC, false));
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setID(String value) {
        this.meta = this.item.getItemMeta();
        PersistentData.set(meta, "id", PersistentDataType.STRING, value);
        this.item.setItemMeta(this.meta);
        return this.setCustomModelData(value);
    }

    public ItemBuilder setFamily(ItemFamily family) {
        this.meta = this.item.getItemMeta();
        PersistentData.set(meta, "family", PersistentDataType.STRING, family.name());
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setRestorable(boolean restorable) {
        this.meta = this.item.getItemMeta();
        PersistentData.set(this.meta, "RESTORABLE", PersistentDataType.BOOLEAN, restorable);
        this.addLore("restorable");
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setCustomData(String key, String value) {
        this.meta = this.item.getItemMeta();
        PersistentData.set(meta, key, PersistentDataType.STRING, value);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemBuilder setLore(String text) {return this.setLore(text, "#b6b6b6");}

    public ItemBuilder setLore(String text, String color) {
        this.meta = this.item.getItemMeta();
        final var lore = new ArrayList<Component>();
        if (this.meta.lore() != null) lore.addAll(this.meta.lore());
        for (var lines : this.splitLoreLines(text, color)) lore.add(MiniMessage.miniMessage().deserialize(lines).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        this.meta.lore(lore);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder addLore(String value) {
        final var lore = new ArrayList<Component>();
        this.meta = this.item.getItemMeta();
        if (this.meta.lore() != null) lore.addAll(Objects.requireNonNull(this.meta.lore()));
        lore.add(MiniMessage.miniMessage().deserialize(value).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        this.meta.lore(lore);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setLodestone(Location location) {
        if (this.item.getType() != Material.COMPASS) return this;

        this.meta = this.item.getItemMeta();
        if (!(meta instanceof CompassMeta compassMeta)) return this;

        compassMeta.clearLodestone();
        compassMeta.setLodestone(location);
        compassMeta.setLodestoneTracked(true);

        this.item.setItemMeta(compassMeta);
        return this;
    }

    public ItemBuilder setPotion(PotionType baseType) {
        if (item.getType() != Material.TIPPED_ARROW) return this;

        this.meta = this.item.getItemMeta();
        if (!(meta instanceof PotionMeta potionMeta)) return this;

        potionMeta.setBasePotionType(baseType);

        this.item.setItemMeta(potionMeta);
        return this;
    }

    public ItemBuilder setOwner(String ownerName) {
        final var meta = (SkullMeta) this.item.getItemMeta();
        final var player = Bukkit.getOfflinePlayer(ownerName);
        meta.setOwningPlayer(player);
        PersistentData.set(meta, "owner", PersistentDataType.STRING, ownerName);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int amplified) {
        this.meta = this.item.getItemMeta();
        this.meta.addEnchant(enchantment, amplified, true);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder removeEnchant(Enchantment enchantment) {
        this.meta = this.item.getItemMeta();
        this.meta.removeEnchant(enchantment);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.meta = this.item.getItemMeta();
        this.meta.setUnbreakable(unbreakable);
        this.meta.setDamageResistant(DamageTypeTags.IS_EXPLOSION);
        this.meta.setDamageResistant(DamageTypeTags.IS_FIRE);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setGlint(boolean glint) {
        this.meta = this.item.getItemMeta();
        if (glint) {
            this.meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            this.meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        else {
            this.meta.removeEnchant(Enchantment.UNBREAKING);
            this.meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setFlag(ItemFlag... flag) {
        this.meta = this.item.getItemMeta();
        this.meta.addItemFlags(flag);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setLeatherColor(Color color) {
        this.meta = this.item.getItemMeta();
        if (!item.getType().name().contains("LEATHER")) return this;

        LeatherArmorMeta leatherMeta = (LeatherArmorMeta) this.meta;
        leatherMeta.setColor(color);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setRocketPower(int power) {
        FireworkMeta fireworkMeta = (FireworkMeta) this.item.getItemMeta();
        if (meta != null) {
            fireworkMeta.setPower(power);
            this.item.setItemMeta(fireworkMeta);
        }
        return this;
    }

    public net.minecraft.world.item.ItemStack buildAsNMS() {
        return CraftItemStack.asNMSCopy(this.item);
    }

    public ItemStack build() {return this.item;}

    public boolean hasLodestone() {
        if (this.item.getType() != Material.COMPASS) return false;

        this.meta = this.item.getItemMeta();
        if (!(meta instanceof CompassMeta compassMeta)) return false;

        return compassMeta.hasLodestone();
    }

    public boolean isRestorable() {return Boolean.TRUE.equals(PersistentData.get(this.item.getItemMeta(), "RESTORABLE", PersistentDataType.BOOLEAN));}

    public String getID() {return PersistentData.get(this.item.getItemMeta(), "id", PersistentDataType.STRING);}

    public ItemFamily getFamily() {
        var familyId = PersistentData.get(this.item.getItemMeta(), "family", PersistentDataType.STRING);
        if (familyId == null || familyId.isEmpty()) return null;
        return ItemFamily.valueOf(familyId.toUpperCase());
    }

    public String getCustomData(String key) {return PersistentData.get(this.item.getItemMeta(), key, PersistentDataType.STRING);}

    public String getOwner() {return PersistentData.get(this.item.getItemMeta(), "owner", PersistentDataType.STRING);}

    public ItemBuilder setCustomModelData(String... values) {
        net.minecraft.world.item.ItemStack nmsItem = this.buildAsNMS();
        ArrayList<String> valuesList = new ArrayList<>(Arrays.asList(values));
        net.minecraft.world.item.component.CustomModelData customModelData = new CustomModelData(new ArrayList<>(), new ArrayList<>(), valuesList, new ArrayList<>());
        nmsItem.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_MODEL_DATA, customModelData).build());

        this.meta = nmsItem.asBukkitCopy().getItemMeta();
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setNbtData(String key, String value) {
        CompoundTag tag = new CompoundTag();
        tag.store(key, Codec.STRING, value);
        net.minecraft.world.item.ItemStack nmsItem = this.buildAsNMS();
        nmsItem.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(tag)).build());
        this.meta = nmsItem.asBukkitCopy().getItemMeta();
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setNbtData(String key, int value) {
        CompoundTag tag = new CompoundTag();
        tag.store(key, Codec.INT, value);
        net.minecraft.world.item.ItemStack nmsItem = this.buildAsNMS();
        nmsItem.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(tag)).build());
        this.meta = nmsItem.asBukkitCopy().getItemMeta();
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder setCooldown(int cooldownTicks) {
        this.meta = this.item.getItemMeta();
        if (this.meta == null) return this;

        PersistentData.set(meta, "COOLDOWN", PersistentDataType.INTEGER, cooldownTicks);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public ItemBuilder addTrims(ArmorTrim trim) {
        this.meta = this.item.getItemMeta();
        if (!(this.meta instanceof ArmorMeta armorMeta)) return this;
        armorMeta.setTrim(trim);
        this.item.setItemMeta(this.meta);
        return this;
    }

    public int getCooldown() {
        this.meta = this.item.getItemMeta();
        if (this.meta == null) return 0;
        return PersistentData.get(this.meta, "COOLDOWN", PersistentDataType.INTEGER);
    }

    @NotNull
    private ItemStack createCustomHead(String base64Texture) {
        final var playerHead = new ItemStack(Material.PLAYER_HEAD);
        final var skullMeta = (SkullMeta) playerHead.getItemMeta();

        GameProfile profile = new GameProfile(UUID.randomUUID(), "custom_head");
        profile.getProperties().put("textures", new Property("textures", base64Texture));
        try {
            final var profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        }
        catch (NoSuchFieldException  | IllegalAccessException e) {e.fillInStackTrace();}

        playerHead.setItemMeta(skullMeta);
        return playerHead;
    }

    private @NotNull List<String> splitLoreLines(@NotNull String text, String color) {
        final var lore = new ArrayList<String>();
        final var builder = new StringBuilder();
        var line = "";
        for (var word : text.split("\\s+")) {
            if (builder.length() + word.length() + 1 > 28) {
                lore.add(line);
                builder.setLength(0);
            }
            builder.append(word).append(" ");
            line = color + builder.toString().trim();
        }
        if (!builder.isEmpty()) lore.add(line);
        return lore;
    }

    public static @NotNull ItemBuilder stackToBuilder(ItemStack stack) {
        return new ItemBuilder(stack);
    }

    @Override
    public ItemBuilder clone() {
        try {
            return (ItemBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
