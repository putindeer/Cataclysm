package org.cataclysm.api.inventory;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.item.ItemBuilder;

public enum MenuItems {
    BLANK(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).setDisplay(" ").setID("blank")),
    BLANK_GRAY(new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplay(" ").setID("blank")),
    BLANK_ORANGE(new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).setDisplay(" ").setID("blank")),
    BLANK_RED(new ItemBuilder(Material.RED_STAINED_GLASS_PANE).setDisplay(" ").setID("blank")),

    NEXT(new ItemBuilder(Material.SPECTRAL_ARROW).setDisplay("Página Siguiente").setID("next").setGlint(true)),
    PREVIOUS(new ItemBuilder(Material.ARROW).setDisplay("Página Anterior").setID("previous").setGlint(true)),

    ;

    private final ItemBuilder item;

    MenuItems(ItemBuilder builder) {this.item = builder;}

    public ItemStack build() {return item.build();}
}