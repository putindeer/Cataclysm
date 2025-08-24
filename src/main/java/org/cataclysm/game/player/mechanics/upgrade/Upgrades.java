package org.cataclysm.game.player.mechanics.upgrade;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.global.utils.text.TextUtils;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

@Getter
public enum Upgrades {

    REGENERATION(11, Material.APPLE, new UpgradeDefinition("regeneration", 0, 1)),
    JUMP_BOOST(12, Material.FROG_SPAWN_EGG, new UpgradeDefinition("jump_boost", 1)),
    SPEED(13, Material.RABBIT_FOOT, new UpgradeDefinition("speed", 1, 3)),
    STRENGTH(14, Material.IRON_SWORD, new UpgradeDefinition("strength", 0, 1, 2)),
    DOLPHIN_GRACE(15, Material.DOLPHIN_SPAWN_EGG, new UpgradeDefinition("dolphin_grace", 0, 1, 2)),

    HEALTH_BLESSING(20, Material.GOLDEN_APPLE, new UpgradeDefinition("health_blessing", 2, 4, 6)),
    RESISTANCE(21, Material.IRON_CHESTPLATE, new UpgradeDefinition("resistance", 0, 1, 2)),
    HERO_OF_THE_VILLAGE(22, Material.EMERALD, new UpgradeDefinition("hero_of_the_village", 4)),
    HASTE(23, Material.GOLDEN_PICKAXE, new UpgradeDefinition("haste", 0, 1, 2)),
    INVISIBILITY(24, Material.GOLDEN_CARROT, new UpgradeDefinition("invisibility", 0)),

    FIRE_RESISTANCE(30, Material.MAGMA_CREAM, new UpgradeDefinition("fire_resistance", 0)),
    CONDUIT_POWER(31, Material.HEART_OF_THE_SEA, new UpgradeDefinition("conduit_power", 0)),
    SLOW_FALLING(32, Material.PHANTOM_MEMBRANE, new UpgradeDefinition("slow_falling", 0)),

    CLEANSING(40, Material.MILK_BUCKET, new UpgradeDefinition("cleansing", 0, 1)),

    ;

    private final int catalogueSlot;
    private final Material material;
    private final UpgradeDefinition definition;

    Upgrades(int catalogueSlot, Material material, UpgradeDefinition definition) {
        this.catalogueSlot = catalogueSlot;
        this.material = material;
        this.definition = definition;
    }

    public ItemStack buildItemStack(Player player) {
        var itemBuilder = new ItemBuilder(this.material);

        var key = TextUtils.formatKey(this.name());
        itemBuilder.setDisplay("<#b9b9b9>" + TinyCaps.tinyCaps(key));

        var levels = this.definition.levels();

        var upgrades = 0;
        for (var level : levels) {
            var prefix = "<#c4c4c4>☐ ";

            var index = (this.findIndex(levels, level) + 1);
            var upgradeManager = CataclysmPlayer.getCataclysmPlayer(player).getUpgradeManager();
            if (upgradeManager.getUpgradeLevel(this) >= index) {
                prefix = "<#d7a24b>☑ <#7c7c7c><st>";
                upgrades++;
            }

            itemBuilder.addLore(prefix + TextUtils.formatKey(this.definition.key()) + " " + TextUtils.toRoman(level + 1));
        }

        if (upgrades >= levels.length) {
            itemBuilder.setDisplay("<#d78f4b><b>" + TinyCaps.tinyCaps(key));
            itemBuilder.setGlint(true);
        }

        return itemBuilder.setID(this.name()).build();
    }

    public static int getWeekUpgrades() {
        return (((Cataclysm.getDay() / 7) + 1) * 2);
    }

    private int findIndex(int @NotNull [] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) return i;
        }
        return -1;
    }
}
