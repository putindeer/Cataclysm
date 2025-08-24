package org.cataclysm.game.block.arcane.table;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.block.arcane.table.events.PlayerUseArcaneTableEvent;
import org.cataclysm.game.block.arcane.table.recipes.ArcaneTableRecipes;
import org.cataclysm.game.items.ItemFamily;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ArcaneTableManager {
    public static final ItemStack STACK_REQUIRE = new ItemStack(Material.DIAMOND_BLOCK, 32);
    public static final int LEVELS_REQUIRE = 15;

    private final Inventory inventory;
    private final Player player;

    public ArcaneTableManager(@NotNull Inventory inventory) {
        this.inventory = inventory;
        this.player = (Player) inventory.getHolder();
    }

    public void forge() {
        if (!this.player.getInventory().containsAtLeast(STACK_REQUIRE, 32) || this.player.getLevel() < LEVELS_REQUIRE) {
            this.player.sendActionBar(MiniMessage.miniMessage().deserialize("<#a53838>Necesitas 32 bloques de diamante y 15 niveles de experiencia."));
            this.player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 1.55F));
            return;
        }

        var baseStack = this.inventory.getItem(ArcaneTableMenu.BASE_SLOT);
        var upgradeStack = this.inventory.getItem(ArcaneTableMenu.UPGRADE_SLOT);
        var materialStack = this.inventory.getItem(ArcaneTableMenu.MATERIAL_SLOT);

        if (baseStack == null || upgradeStack == null || materialStack == null) return;

        for (var recipe : ArcaneTableRecipes.values()) {
            if (!recipe.getBase().getType().equals(baseStack.getType())) continue;

            var ingredients = recipe.getIngredients();
            if (!Objects.equals(ingredients.getUpgrader().getBuilder().getID(), new ItemBuilder(upgradeStack).getID())) continue;

            var recipeMaterialStack = ingredients.getMaterialStack();

            var recipeMaterialAmount = recipeMaterialStack.getAmount();
            var materialStackAmount = materialStack.getAmount();

            if (recipeMaterialStack.getType() != materialStack.getType() || recipeMaterialAmount > materialStackAmount) continue;

            var rawResult = recipe.getResult();
            var recipeResult = rawResult.build();
            var itemFamily = rawResult.getBuilder().getFamily();

            if (itemFamily != null) {
                switch (itemFamily) {
                    case ItemFamily.TWISTED_TOOLS -> {
                        if (Cataclysm.getDay() >= 14) {
                            this.player.sendActionBar(MiniMessage.miniMessage().deserialize("<#a53838>No se pueden craftear TWISTED TOOLS"));
                            this.player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 1.55F));
                            return;
                        }
                    }

                    case ItemFamily.ARCANE_TOOLS -> {

                    }

                    //Add calamity as item family or something lol
                }
            }


            var resultToAdd = recipeResult.clone();
            baseStack.getEnchantments().forEach((enchantment, integer) -> {
                if (!recipeResult.getEnchantments().containsKey(enchantment)) {
                    resultToAdd.addEnchantment(enchantment, integer);
                }
            });

            baseStack.setAmount(baseStack.getAmount() - 1);
            upgradeStack.setAmount(upgradeStack.getAmount() - 1);
            materialStack.setAmount(materialStackAmount - recipeMaterialAmount);

            this.chargeDefaults();
            this.playForgeSound();

            this.player.getInventory().addItem(resultToAdd);
            resultToAdd.removeEnchantments();

            new PlayerUseArcaneTableEvent(this.player, resultToAdd).callEvent();
        }
    }

    private void chargeDefaults() {
        this.player.setLevel(this.player.getLevel() - LEVELS_REQUIRE);
        boolean hasConsumed = false;

        for (var content : this.player.getInventory().getContents()) {
            if (content == null
                    || content.getType().isAir()
                    || content.getType() != STACK_REQUIRE.getType()
                    || content.getAmount() < STACK_REQUIRE.getAmount()
            ) continue;

            if (!hasConsumed) {
                content.setAmount(content.getAmount() - STACK_REQUIRE.getAmount());
                hasConsumed = true;
            }
        }
    }

    private void playForgeSound() {
        this.player.playSound(Sound.sound(Key.key("block.enchantment_table.use"), Sound.Source.BLOCK, 1.0F, 0.65F));
        this.player.playSound(Sound.sound(Key.key("block.anvil.use"), Sound.Source.BLOCK, 1.0F, 0.75F));
        this.player.playSound(Sound.sound(Key.key("item.mace.smash_ground_heavy"), Sound.Source.BLOCK, 0.6F, 1.9F));
    }
}
