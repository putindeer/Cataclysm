package org.cataclysm.game.block.arcane.table.recipes;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.game.items.CataclysmItems;

@Getter
public enum ArcaneTableIngredients {
    TWISTED_RECIPE(CataclysmItems.TWISTED_INGOT, new ItemStack(Material.CRYING_OBSIDIAN, 32)),
    ARCANE_RECIPE(CataclysmItems.ARCANE_INGOT, new ItemStack(Material.GOLD_BLOCK, 32)),
    ARCANE_MACE_RECIPE(CataclysmItems.ARCANE_ROD, new ItemStack(Material.GOLD_BLOCK, 32)),
    CALAMITY_ARMOR_RECIPE(CataclysmItems.CALAMITY_INGOT, new ItemStack(Material.ANCIENT_DEBRIS, 32)),
    MIRAGE_ELYTRA_RECIPE(CataclysmItems.MIRAGE_UPGRADE, new ItemStack(Material.NETHERITE_BLOCK, 32)),
    MIRAGE_MACE_RECIPE(CataclysmItems.MACE_UPGRADE, new ItemStack(Material.NETHERITE_BLOCK, 32))
    ;

    private final CataclysmItems upgrader;
    private final ItemStack materialStack;

    ArcaneTableIngredients(CataclysmItems upgrader, ItemStack materialStack) {
        this.upgrader = upgrader;
        this.materialStack = materialStack;
    }
}