package org.cataclysm.game.mob.custom.vanilla.skeleton.wither;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NetherNightmare extends CataclysmMob {

    public NetherNightmare(Level level) {
        super(new NetherNightmareEntity(level), "Nether Nightmare", "#361614", level);
        super.setHealth(100);
        super.setAttribute(Attributes.SCALE, 1.25);
        super.setItem(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(this.nightmareBanner()));
        super.setItem(EquipmentSlot.CHEST, Items.NETHERITE_CHESTPLATE);
        super.setItem(EquipmentSlot.LEGS, Items.NETHERITE_LEGGINGS);
        super.setItem(EquipmentSlot.FEET, Items.NETHERITE_BOOTS);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 200).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.NIGHTMARE_BONE.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new NetherNightmare(super.getLevel());
    }

    static class NetherNightmareEntity extends WitherSkeleton {

        public NetherNightmareEntity(Level level) {
            super(EntityType.WITHER_SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    private ItemStack nightmareBanner() {
        var list = new ArrayList<Pattern>();
        list.add(new Pattern(DyeColor.ORANGE, PatternType.STRAIGHT_CROSS));
        list.add(new Pattern(DyeColor.BLACK, PatternType.CROSS));
        list.add(new Pattern(DyeColor.RED, PatternType.SMALL_STRIPES));
        list.add(new Pattern(DyeColor.BLACK, PatternType.BORDER));
        list.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_TOP));
        list.add(new Pattern(DyeColor.BLACK, PatternType.STRIPE_BOTTOM));
        return this.createCustomBanner(Material.RED_BANNER, list);
    }

    private @NotNull ItemStack createCustomBanner(@NotNull Material bannerMaterial, List<Pattern> patterns) {
        if (!bannerMaterial.name().endsWith("_BANNER")) {
            throw new IllegalArgumentException("Material must be a BANNER type. Given: " + bannerMaterial.name());
        }

        ItemStack banner = new ItemStack(bannerMaterial);
        BannerMeta meta = (BannerMeta) banner.getItemMeta();

        if (meta == null) throw new IllegalStateException("Failed to get BannerMeta for material: " + bannerMaterial.name());

        meta.setPatterns(patterns);
        banner.setItemMeta(meta);
        return banner;
    }
}
