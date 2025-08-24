package org.cataclysm.game.block.arcane.table.recipes;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.game.items.CataclysmItems;

@Getter
public enum ArcaneTableRecipes {
    TWISTED_SWORD_RECIPE(CataclysmItems.TWISTED_SWORD, Material.NETHERITE_SWORD, ArcaneTableIngredients.TWISTED_RECIPE),
    TWISTED_PICKAXE_RECIPE(CataclysmItems.TWISTED_PICKAXE, Material.NETHERITE_PICKAXE, ArcaneTableIngredients.TWISTED_RECIPE),
    TWISTED_AXE_RECIPE(CataclysmItems.TWISTED_AXE, Material.NETHERITE_AXE, ArcaneTableIngredients.TWISTED_RECIPE),
    TWISTED_SHOVEL_RECIPE(CataclysmItems.TWISTED_SHOVEL, Material.NETHERITE_SHOVEL, ArcaneTableIngredients.TWISTED_RECIPE),
    TWISTED_HOE_RECIPE(CataclysmItems.TWISTED_HOE, Material.NETHERITE_HOE, ArcaneTableIngredients.TWISTED_RECIPE),

    ARCANE_BOW_RECIPE(CataclysmItems.ARCANE_BOW, Material.BOW, ArcaneTableIngredients.ARCANE_RECIPE),
    ARCANE_SHIELD_RECIPE(CataclysmItems.ARCANE_SHIELD, Material.SHIELD, ArcaneTableIngredients.ARCANE_RECIPE),
    ARCANE_TRIDENT_RECIPE(CataclysmItems.ARCANE_TRIDENT, Material.TRIDENT, ArcaneTableIngredients.ARCANE_RECIPE),
    ARCANE_TOTEM_RECIPE(CataclysmItems.ARCANE_TOTEM, Material.TOTEM_OF_UNDYING, ArcaneTableIngredients.ARCANE_RECIPE),
    ARCANE_MACE_RECIPE(CataclysmItems.ARCANE_MACE, CataclysmItems.ARCANE_CORE.build(), ArcaneTableIngredients.ARCANE_MACE_RECIPE),

    CALAMITY_HELMET_RECIPE(CataclysmItems.CALAMITY_HELMET, Material.NETHERITE_HELMET, ArcaneTableIngredients.CALAMITY_ARMOR_RECIPE),
    CALAMITY_CHESTPLATE_RECIPE(CataclysmItems.CALAMITY_CHESTPLATE, Material.NETHERITE_CHESTPLATE, ArcaneTableIngredients.CALAMITY_ARMOR_RECIPE),
    CALAMITY_LEGGINGS_RECIPE(CataclysmItems.CALAMITY_LEGGINGS, Material.NETHERITE_LEGGINGS, ArcaneTableIngredients.CALAMITY_ARMOR_RECIPE),
    CALAMITY_BOOTS_RECIPE(CataclysmItems.CALAMITY_BOOTS, Material.NETHERITE_BOOTS, ArcaneTableIngredients.CALAMITY_ARMOR_RECIPE),

    MIRAGE_ELYTRA_RECIPE(CataclysmItems.MIRAGE_ELYTRA, CataclysmItems.BROKEN_ELYTRA.build(), ArcaneTableIngredients.MIRAGE_ELYTRA_RECIPE),
    MIRAGE_MACE_RECIPE(CataclysmItems.MIRAGE_MACE, CataclysmItems.ARCANE_MACE.build(), ArcaneTableIngredients.MIRAGE_MACE_RECIPE)

    ;

    private final CataclysmItems result;
    private final ItemStack base;
    private final ArcaneTableIngredients ingredients;

    ArcaneTableRecipes(CataclysmItems result, Material base, ArcaneTableIngredients ingredients) {
        this(result, new ItemStack(base), ingredients);
    }

    ArcaneTableRecipes(CataclysmItems result, ItemStack base, ArcaneTableIngredients ingredients) {
        this.result = result;
        this.base = base;
        this.ingredients = ingredients;
    }
}
