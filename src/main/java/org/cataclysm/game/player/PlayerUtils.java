package org.cataclysm.game.player;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class PlayerUtils {

    public static void breakElytras(Player player, int cooldown) {
        if (player.hasPotionEffect(ImmunityEffect.EFFECT_TYPE)) return;
        PlayerInventory inventory = player.getInventory();

        ItemStack chestplate = inventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.ELYTRA) return;

        ItemStack elytra = chestplate.clone();
        if (inventory.contains(Material.AIR)) inventory.addItem(elytra);
        else {
            boolean replace = false;
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item != null && item.getType().isAir()) {
                    replace = true;
                    inventory.setItem(i, elytra);
                    break;
                }
            }
            if (!replace) {
                var item = player.getWorld().dropItemNaturally(player.getLocation(), elytra);
                item.setInvulnerable(true);
                item.setGlowing(true);
                item.setPickupDelay(0);
            }
            chestplate.setAmount(0);
            if (cooldown != 0) player.setCooldown(Material.ELYTRA, cooldown);
            player.playSound(player, Sound.ITEM_SHIELD_BREAK, 1, 1);
        }
    }

    public static double getMaxHealth(@NotNull Player player) {
        var attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute != null) return attribute.getValue();
        return 0.0;
    }

    public static void setMaxHealth(@NotNull Player player, double health) {
        var attribute = player.getAttribute(Attribute.MAX_HEALTH);
        if (attribute == null || attribute.getValue() < 1) return;
        attribute.setBaseValue(health);
    }

    public static void operateMaxHealth(Player player, double amount) {
        setMaxHealth(player, getMaxHealth(player) + amount);
    }

    public static void cancelSleep(@NotNull PlayerBedEnterEvent event) {
        var bed = event.getBed();
        bed.getWorld().spawnParticle(Particle.EXPLOSION_EMITTER, bed.getLocation(), 1);
        bed.getWorld().playSound(bed.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1, 1);

        if (Cataclysm.getDay() < 21) resetInsomnia(event.getPlayer());
        event.setCancelled(true);
    }

    public static void resetInsomnia(@NotNull Player player) {
        if (Cataclysm.getRagnarok() != null && Cataclysm.getRagnarok().getData().getLevel() >= 5) {
            ChatMessenger.sendMessage(player, ChatMessenger.getTextColor() + "No se puede reiniciar la insomnia en Ragnarok de nivel 5.");
            return;
        }
        player.setStatistic(Statistic.TIME_SINCE_REST, 0);
        ChatMessenger.sendMessage(player, ChatMessenger.getTextColor() + "Tu insomnia se ha reiniciado.");
    }

    public static boolean hasArmor(ItemFamily family, Player player) {
        var inventory = player.getInventory();

        var head = inventory.getHelmet();
        var chest = inventory.getChestplate();
        var legs = inventory.getLeggings();
        var feet = inventory.getBoots();

        return isFamilyMember(family, head, chest, legs, feet);
    }

    public static boolean hasTwistedHoe(@NotNull Player player) {
        var inventory = player.getInventory();
        ItemStack hand = null;
        if (inventory.getItemInMainHand().getType() == Material.NETHERITE_HOE) hand = inventory.getItemInMainHand();
        if (inventory.getItemInOffHand().getType() == Material.NETHERITE_HOE) hand = inventory.getItemInMainHand();
        if (hand == null) return false;
        if (hand.getType() != Material.NETHERITE_HOE) return false;
        if (hand.getItemMeta() == null) return false;
        return hand.getItemMeta().isUnbreakable();
    }

    public static boolean hasMirageHelmet(@NotNull Player player) {
        var inventory = player.getInventory();
        if (Objects.requireNonNull(inventory.getHelmet()).getType() != Material.TURTLE_HELMET) return false;
        ItemStack head = inventory.getHelmet();
        if (head.getItemMeta() == null) return false;
        return head.getItemMeta().isUnbreakable();
    }

    private static boolean isFamilyMember(ItemFamily itemFamily, ItemStack @NotNull ... items) {
        for (ItemStack item : items) {
            ItemBuilder builder = new ItemBuilder(item);
            if (builder.getFamily() == null || !builder.getFamily().equals(itemFamily)) return false;
        }
        return true;
    }

    public static void fixItem(Player player, ItemStack fixedItem) {
        var inventory = player.getInventory();
        for (var itemStack : inventory.getContents()) {
            if (itemStack == null) continue;
            if (itemStack.getType().isAir()) continue;

            var itemMeta = itemStack.getItemMeta();
            if (itemMeta == null) continue;
            if (!itemMeta.isUnbreakable()) continue;

            if (itemMeta instanceof ArmorMeta armorMeta) {
                armorMeta.setTrim(new ArmorTrim(TrimMaterial.IRON, TrimPattern.SPIRE));
                itemStack.setItemMeta(armorMeta);
                continue;
            }

            if (itemStack.getType() != fixedItem.getType()) continue;
            switch (fixedItem.getType()) {
                case NETHERITE_SWORD -> {
                    itemStack.addUnsafeEnchantment(Enchantment.SHARPNESS, 7);
                    itemStack.addUnsafeEnchantment(Enchantment.SMITE, 7);
                    itemStack.addUnsafeEnchantment(Enchantment.BANE_OF_ARTHROPODS, 7);
                    return;
                }

                case NETHERITE_AXE -> {
                    if (itemMeta.getAttributeModifiers() == null) return;
                    if (itemMeta.getAttributeModifiers().containsKey(Attribute.ATTACK_SPEED)) return;
                    itemMeta.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(new NamespacedKey(Cataclysm.getInstance(), "fixed.attackSpeed"), -3.0, AttributeModifier.Operation.ADD_NUMBER));
                    itemStack.setItemMeta(itemMeta);
                }
            }

        }
    }


}
