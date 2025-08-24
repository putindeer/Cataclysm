package org.cataclysm.game.block.arcane.table;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.global.utils.text.font.TinyCaps;

public enum ArcaneTableItems {

    MATERIALS_SLOT(new ItemBuilder(Material.HONEYCOMB)
            .setDisplay("<#c4b478>↓ " + TinyCaps.tinyCaps("Materials Slot"))
            .setLore("Coloque los bloques u objetos indicados en la receta.", "<#c2b998>")
            .setGlint(true)
            .setID("info")),

    BASE_SLOT(new ItemBuilder(Material.BLADE_POTTERY_SHERD)
            .setDisplay("<#b18756>↓ " + TinyCaps.tinyCaps("Tool Slot"))
            .setLore("Coloque la herramienta indicada en la receta", "<#b29678>")
            .addLore(" ")
            .setLore("Se detectará el objeto original, ya sea uno especial o vanilla.", "<#af9b85>")
            .setGlint(true)
            .setID("info")),

    UPGRADE_SLOT(new ItemBuilder(Material.NETHERITE_SCRAP)
            .setDisplay("<#ab5a5a>↓ " + TinyCaps.tinyCaps("Upgrade Slot"))
            .setLore("Coloque el modificador adecuado para alterar las propiedades de la herramienta base.", "<#977979>")
            .setGlint(true)
            .setID("info")),

    FORGE(new ItemBuilder(Material.ANVIL)
            .setDisplay("<#cf9e56>\uD83D\uDD25 " + TinyCaps.tinyCaps("Forge"))
            .setLore("Active la forja para iniciar el proceso. Todos los elementos deben estar correctamente colocados.", "<#b8a385>")
            .addLore(" ")
            .setLore("Por defecto, se le cobrará lo siguiente:", "<#b8ac9b>")
            .addLore("<#b8a385>• 32 bloques de diamante.")
            .addLore("<#b8a385>• 15 niveles de experiencia.")
            .setID("forge")),

    ;

    private final ItemBuilder builder;

    ArcaneTableItems(ItemBuilder builder) {
        this.builder = builder;
    }

    public ItemStack build() {
        return this.builder.build();
    }
}
