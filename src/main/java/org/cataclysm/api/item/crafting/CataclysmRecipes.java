package org.cataclysm.api.item.crafting;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.crafting.util.CustomRecipe;
import org.cataclysm.game.items.CataclysmItems;

@Getter
public enum CataclysmRecipes {

    //Dia 0
    PARAGON_BLESSING(new CustomRecipe("paragon_blessing", CataclysmItems.PARAGON_BLESSING.build(), 1, true).setShape("QQQ", "QNQ", "QQQ").setIngredient('Q', CataclysmItems.PARAGON_QUARTZ.build()).setIngredient('N', Material.NETHERITE_INGOT), 0),
    TECTONITE(new CustomRecipe("tectonite", CataclysmItems.TECTONITE.build(), 1, true).setShape("III", "I I", "III").setIngredient('I', Material.IRON_BLOCK), 0),
    ONYX(new CustomRecipe("onyx", CataclysmItems.ONYX.build(), 1, true).setShape("ODO", "DCD", "ODO").setIngredient('O', Material.OBSIDIAN).setIngredient('D', Material.DIAMOND_BLOCK).setIngredient('C', Material.CRYING_OBSIDIAN), 0),
    VOID_STONE(new CustomRecipe("void_stone", CataclysmItems.VOID_STONE.build(), 1, true).setShape("DDD", "DND", "DDD").setIngredient('D', CataclysmItems.ONYX.build()).setIngredient('N', Material.NETHERITE_INGOT), 0),
    CATACLYSM_UPGRADE_TIER_1(new CustomRecipe("cataclysm_upgrade_tier_1", CataclysmItems.CATACLYSM_UPGRADE.build(), 1, true).setShape("FFF", "FCF", "FFF").setIngredient('F', CataclysmItems.TECTONITE.build()).setIngredient('C', CataclysmItems.VOID_STONE.build()), 0),

    //Dia 7
    ARCANE_INGOT(new CustomRecipe("arcane_ingot", CataclysmItems.ARCANE_INGOT.build(), 1, true).setShape("FFF", "FCF", "FFF").setIngredient('F', CataclysmItems.ARCANE_NUGGET.build()).setIngredient('C', Material.NETHERITE_INGOT), 7),
    TWISTED_INGOT(new CustomRecipe("twisted_ingot", CataclysmItems.TWISTED_INGOT.build(), 1, true).setShape("FBG", "ONO", "SPR").setIngredient('F', CataclysmItems.TWISTED_FLESH.build()).setIngredient('B', CataclysmItems.TWISTED_BONE.build()).setIngredient('G', CataclysmItems.TWISTED_POWDER.build()).setIngredient('O', Material.CRYING_OBSIDIAN).setIngredient('N', Material.NETHERITE_INGOT).setIngredient('S', CataclysmItems.TWISTED_STRING.build()).setIngredient('P', CataclysmItems.TWISTED_PEARL.build()).setIngredient('R', CataclysmItems.TWISTED_ROD.build()), 7),
    ARCANE_SCUTE(new CustomRecipe("arcane_scute", CataclysmItems.ARCANE_SCUTE.build(), 1, true).setShape("RSR", "SNS", "RNR").setIngredient('R', CataclysmItems.ARCANE_ROD.build()).setIngredient('S', Material.TURTLE_SCUTE).setIngredient('N', CataclysmItems.ARCANE_NUGGET.build()), 7),
    CATACLYSM_UPGRADE_TIER_2(new CustomRecipe("cataclysm_upgrade_tier_2", CataclysmItems.CATACLYSM_UPGRADE_TIER_2.build(), 1, true).setShape("FFF", "FCF", "FFF").setIngredient('F', CataclysmItems.ARCANE_SCUTE.build()).setIngredient('C', CataclysmItems.CATACLYSM_UPGRADE.build()), 7),

    ARCANE_CORE(new CustomRecipe("arcane_core", CataclysmItems.ARCANE_CORE.build(), 1, true)
            .setShape(
                    "III",
                    "ICI",
                    "III"
            )
            .setIngredient('I', CataclysmItems.ARCANE_NUGGET.build())
            .setIngredient('C', Material.HEAVY_CORE),
            7),

    // Dia 10

    VOID(new CustomRecipe("void", CataclysmItems.VOID_HEART.build(), 1, true)
            .setShape(
                    "ABC",
                    "DEF",
                    "GHI"
            )
            .setIngredient('A', Material.SCULK_CATALYST)
            .setIngredient('B', Material.NAUTILUS_SHELL)
            .setIngredient('C', Material.GHAST_TEAR)

            .setIngredient('D', Material.WITHER_SKELETON_SKULL)
            .setIngredient('E', Material.CREAKING_HEART)
            .setIngredient('F', Material.RABBIT_HIDE)

            .setIngredient('G', CataclysmItems.ARCANE_SCUTE.build())
            .setIngredient('H', Material.TUBE_CORAL)
            .setIngredient('I', CataclysmItems.VOID_STONE.build()),
            10),

    TWISTED_RELIC(new CustomRecipe("twisted_relic", CataclysmItems.TWISTED_RELIC.build(), 1, true)
            .setShape(
                    "RTR",
                    "TVT",
                    "RTR"
            )
            .setIngredient('R', Material.RESIN_CLUMP)
            .setIngredient('T', CataclysmItems.TWISTED_INGOT.build())
            .setIngredient('V', CataclysmItems.VOID_HEART.build()),
            10),

    // Dia 21

    CALAMITY_INGOT(new CustomRecipe("calamity_ingot", CataclysmItems.CALAMITY_INGOT.build(), 1, false)
            .setShape(
                    "FFF",
                    "FCF",
                    "FFF")
            .setIngredient('F', CataclysmItems.CALAMITY_NUGGET.build())
            .setIngredient('C', Material.NETHERITE_INGOT), 14),

    MIDAY_RELIC(new CustomRecipe("midway_relic", CataclysmItems.MIDWAY_RELIC.build(), 1, true)
            .setShape(
                    "ABC",
                    "DEF",
                    "GHI"
            )
            .setIngredient('A', CataclysmItems.LLAMA_FUR.build())
            .setIngredient('B', CataclysmItems.UR_TEAR.build())
            .setIngredient('C', CataclysmItems.TOXIC_MEMBRANE.build())

            .setIngredient('D', CataclysmItems.CATACLYST_BONE.build())
            .setIngredient('E', CataclysmItems.TWISTED_RELIC.build())
            .setIngredient('F', CataclysmItems.NIGHTMARE_BONE.build())

            .setIngredient('G', CataclysmItems.GOLDEN_CREAM.build())
            .setIngredient('H', CataclysmItems.GUARDIAN_HEART.build())
            .setIngredient('I', CataclysmItems.PALE_BALL.build()),
            21),

    MIRAGE_SCUTE(new CustomRecipe("mirage_scute", CataclysmItems.MIRAGE_SCUTE.build(), 1, true)
            .setShape(
                    "WAS",
                    "D G",
                    "   "
            )
            .setIngredient('W', CataclysmItems.WANDERING_HEART.build())
            .setIngredient('A', CataclysmItems.ARCANE_SCUTE.build())
            .setIngredient('S', CataclysmItems.WANDERING_SOUL.build())
            .setIngredient('D', CataclysmItems.DROWNED_CROWN.build())
            .setIngredient('G', CataclysmItems.GOLEM_HEAD.build()),
            21),
    MIRAGE_HELMET(new CustomRecipe("mirage_helmet", CataclysmItems.MIRAGE_HELMET.build(), 1, true)
            .setShape(
                    "AMT",
                    "M M",
                    "   "
            )
            .setIngredient('A', CataclysmItems.ARCANE_INGOT.build())
            .setIngredient('M', CataclysmItems.MIRAGE_SCUTE.build())
            .setIngredient('T', CataclysmItems.TWISTED_INGOT.build()),
            21),
    MIRAGE_INGOT(new CustomRecipe("mirage_ingot", CataclysmItems.MIRAGE_INGOT.build(), 1, true)
            .setShape(
                    "TEY",
                    "NSN",
                    "BPF"
            )
            .setIngredient('T', CataclysmItems.MIRAGE_TEAR.build())
            .setIngredient('E', CataclysmItems.MIRAGE_PEARL.build())
            .setIngredient('Y', CataclysmItems.MIRAGE_EYEBALL.build())
            .setIngredient('N', Material.NETHERITE_BLOCK)
            .setIngredient('S', Material.SHULKER_SHELL)
            .setIngredient('B', CataclysmItems.MIRAGE_BONE.build())
            .setIngredient('P', CataclysmItems.MIRAGE_POWDER.build())
            .setIngredient('F', CataclysmItems.MIRAGE_FLESH.build()),
            21),
    MACE_UPGRADE(new CustomRecipe("mace_upgrade", CataclysmItems.MACE_UPGRADE.build(), 1, true)
            .setShape(
                    "CAT",
                    "MUM",
                    "TAC"
            )
            .setIngredient('C', CataclysmItems.CALAMITY_INGOT.build())
            .setIngredient('A', CataclysmItems.ARCANE_CORE.build())
            .setIngredient('T', CataclysmItems.TWISTED_INGOT.build())
            .setIngredient('M', CataclysmItems.MIRAGE_INGOT.build())
            .setIngredient('U', CataclysmItems.CATACLYSM_UPGRADE.build()),
            21),

    MACE_DECRAFT(new CustomRecipe("mace_decraft", ItemStack.of(Material.HEAVY_CORE), 1, true)
            .setShape(
                    "   ",
                    " C ",
                    "   "
            ).setIngredient('C', ItemStack.of(Material.MACE)), 7),
    ENDER_BAG(new CustomRecipe("ender_bag", CataclysmItems.ENDER_BAG.build(), 1, true).setShape("NSN", "SES", "NSN").setIngredient('N', Material.NETHERITE_BLOCK).setIngredient('S', Material.SHULKER_BOX).setIngredient('E', Material.ENDER_EYE), 21),
    PARAGON_PEARL(new CustomRecipe("paragon_pearl", new ItemStack(CataclysmItems.PARAGON_PEARL.build()), 4, true)
            .setShape(
                    "MPM",
                    "PEP",
                    "MPM"
            )
            .setIngredient('M', CataclysmItems.MIRAGE_PEARL.build())
            .setIngredient('P', CataclysmItems.PARAGON_BLESSING.build())
            .setIngredient('E', Material.ENDER_EYE),
            21),
    SHULKER_BOX(new CustomRecipe("shulker_box", ItemStack.of(Material.SHULKER_BOX), 1, true)
            .setShape(
                    "NSN",
                    "SES",
                    "NSN"
            )
            .setIngredient('N', Material.NETHERITE_BLOCK)
            .setIngredient('S', Material.SHULKER_SHELL)
            .setIngredient('E', Material.ENDER_CHEST),
            21),

    OBSCURE_ONYX(new CustomRecipe("obscure_onyx", CataclysmItems.OBSCURE_ONYX.build(), 1, true)
            .setShape(
                    "NNN",
                    "NON",
                    "NNN"
            )
            .setIngredient('O', CataclysmItems.ONYX.build())
            .setIngredient('N', Material.NETHERITE_BLOCK),
            21),

    MIRAGE_AMETHYST(new CustomRecipe("mirage_amethyst", CataclysmItems.MIRAGE_AMETHYST.build(), 1, true)
            .setShape(
                    "SGS",
                    "GAG",
                    "SGS"
            )
            .setIngredient('A', Material.AMETHYST_BLOCK)
            .setIngredient('S', Material.ECHO_SHARD)
            .setIngredient('G', new ItemStack(Material.GILDED_BLACKSTONE, 8)),
            21),

    CATACLYSM_UPGRADE_TIER_3(new CustomRecipe("cataclysm_upgrade_tier_3", CataclysmItems.CATACLYSM_UPGRADE_TIER_3.build(), 1, true)
            .setShape("FFF", "FCF", "FFF")
            .setIngredient('F', CataclysmItems.OBSCURE_ONYX.build())
            .setIngredient('C', CataclysmItems.CATACLYSM_UPGRADE_TIER_2.build()),
            21),

    MIRAGE_UPGRADE(new CustomRecipe("mirage_upgrade", CataclysmItems.MIRAGE_UPGRADE.build(), 1, true)
            .setShape(
                    "MMW",
                    "MOM",
                    "WMM"
            )
            .setIngredient('M', CataclysmItems.MIRAGE_INGOT.build())
            .setIngredient('O', CataclysmItems.OBSCURE_ONYX.build())
            .setIngredient('W', CataclysmItems.WHALE_WING.build()),
            21),
    MIRAGE_ESSENCE(new CustomRecipe("mirage_essence", CataclysmItems.MIRAGE_ESSENCE.build(), 1, true)
            .setShape(
                    "QQQ",
                    "QQQ",
                    "QQQ"
            )
            .setIngredient('Q', CataclysmItems.MIRAGE_QUARTZ.build()),
            21),

    MIRAGE_BLESSING(new CustomRecipe("mirage_blessing", CataclysmItems.MIRAGE_BLESSING.build(), 1, true)
            .setShape(
                    "AMA",
                    "EBE",
                    "AMA"
            )
            .setIngredient('A', CataclysmItems.MIRAGE_AMETHYST.build())
            .setIngredient('M', CataclysmItems.MIRAGE_INGOT.build())
            .setIngredient('E', CataclysmItems.MIRAGE_ESSENCE.build())
            .setIngredient('B', CataclysmItems.PARAGON_BLESSING.build()),
            21),

    MIRAGE_APPLE(new CustomRecipe("mirage_apple", CataclysmItems.MIRAGE_APPLE.build(), 1, true)
            .setShape(
                    "NMN",
                    "MNM",
                    "NMN"
            )
            .setIngredient('N', CataclysmItems.ENCHANTED_CALAMITY_APPLE.build())
            .setIngredient('M', CataclysmItems.MIRAGE_INGOT.build()),
            21)
    ;

    private final CustomRecipe customRecipe;
    private final int unlockDay;

    CataclysmRecipes(CustomRecipe customRecipe, int unlockDay) {
        this.customRecipe = customRecipe;
        this.unlockDay = unlockDay;
    }

    public static void updateRecipes() {
        int day = Cataclysm.getDay();
        for (CataclysmRecipes recipe : values()) {
            CustomRecipe customRecipe = recipe.getCustomRecipe();
            NamespacedKey key = new NamespacedKey(Cataclysm.getInstance(), customRecipe.getId());
            if (Bukkit.getRecipe(key) != null) {
                if (recipe.getUnlockDay() <= day) continue;
                else Bukkit.removeRecipe(key);
            }

            if (recipe.getUnlockDay() <= day) customRecipe.register();
        }

        removeVanillaRecipes();
    }

    private static void removeVanillaRecipes() {
        // Remove Shulker Box
        var recipe = Bukkit.getServer().getRecipe(NamespacedKey.minecraft("shulker_box"));
        if (recipe != null) Bukkit.getServer().removeRecipe(NamespacedKey.minecraft("shulker_box"));
    }

}
