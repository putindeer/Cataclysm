package org.cataclysm.game.block.calamity.vault.drops;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public enum CalamityVaultDrops {
    ANCIENT_DEBRIS_1(CalamityVaultRarity.COMMON, new ItemStack(Material.ANCIENT_DEBRIS, 4)),
    ANCIENT_DEBRIS_2(CalamityVaultRarity.COMMON, new ItemStack(Material.ANCIENT_DEBRIS, 8)),
    CALAMITY_CARROT_1(CalamityVaultRarity.COMMON, CataclysmItems.CALAMITY_CARROT.cloneBuilder().setAmount(3).build()),
    CALAMITY_CARROT_2(CalamityVaultRarity.COMMON, CataclysmItems.CALAMITY_CARROT.cloneBuilder().setAmount(6).build()),
    CALAMITY_APPLE_1(CalamityVaultRarity.COMMON, CataclysmItems.CALAMITY_APPLE.cloneBuilder().setAmount(4).build()),
    CALAMITY_APPLE_2(CalamityVaultRarity.COMMON, CataclysmItems.CALAMITY_APPLE.cloneBuilder().setAmount(8).build()),
    CALAMITY_NUGGET_1(CalamityVaultRarity.COMMON, CataclysmItems.CALAMITY_NUGGET.cloneBuilder().setAmount(1).build()),
    NETHERITE_INGOT(CalamityVaultRarity.COMMON, new ItemStack(Material.NETHERITE_INGOT, 3)),

    TURTLE_SCUTE_1(CalamityVaultRarity.RARE, new ItemStack(Material.TURTLE_SCUTE, 16)),
    CALAMITY_APPLE_3(CalamityVaultRarity.RARE, CataclysmItems.CALAMITY_APPLE.cloneBuilder().setAmount(8).build()),
    CALAMITY_APPLE_4(CalamityVaultRarity.RARE, CataclysmItems.CALAMITY_APPLE.cloneBuilder().setAmount(10).build()),
    CALAMITY_NUGGET_2(CalamityVaultRarity.RARE, CataclysmItems.CALAMITY_NUGGET.cloneBuilder().setAmount(3).build()),
    CATACLYSM_UPGRADE_LEVEL_2(CalamityVaultRarity.RARE, CataclysmItems.CATACLYSM_UPGRADE_TIER_2.build()),
    NETHERITE_BLOCK(CalamityVaultRarity.RARE, new ItemStack(Material.NETHERITE_BLOCK, 1)),

    ENCHANTED_CALAMITY_APPLE(CalamityVaultRarity.EPIC, CataclysmItems.ENCHANTED_CALAMITY_APPLE.cloneBuilder().setAmount(3).build()),
    ARCANE_CORE(CalamityVaultRarity.EPIC, CataclysmItems.ARCANE_CORE.build()),

    CALAMITY_TOTEM(CalamityVaultRarity.LEGENDARY, CataclysmItems.CALAMITY_TOTEM.build()),
    PARAGONS_BLESSING(CalamityVaultRarity.LEGENDARY, CataclysmItems.PARAGON_BLESSING.build());

    private final ItemStack stack;
    private final CalamityVaultRarity rarity;

    CalamityVaultDrops(CalamityVaultRarity rarity, ItemStack stack) {
        this.stack = stack;
        this.rarity = rarity;
    }

    public static @NotNull List<ItemStack> createLootTable(int amount) {
        var list = new ArrayList<ItemStack>();
        for (var rarity : CalamityVaultRarity.getRandomizedRarities(amount)) {
            var members = getRarityMembers(rarity);
            Collections.shuffle(members);
            list.add(members.getFirst().stack);
        }
        return list;
    }

    private static @NotNull List<CalamityVaultDrops> getRarityMembers(CalamityVaultRarity rarity) {
        var list = new ArrayList<CalamityVaultDrops>();
        for (var drop : CalamityVaultDrops.values()) {
            if (drop.rarity != rarity) continue;
            list.add(drop);
        }
        return list;
    }
}
