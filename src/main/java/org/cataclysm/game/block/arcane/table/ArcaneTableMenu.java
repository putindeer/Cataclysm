package org.cataclysm.game.block.arcane.table;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.inventory.MenuItems;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class ArcaneTableMenu extends BasicMenu {
    public static final int MATERIAL_SLOT = 11;
    public static final int BASE_SLOT = 13;
    public static final int UPGRADE_SLOT = 15;

    private final ArcaneTableManager table;

    public ArcaneTableMenu(Player player) {
        super(27, TinyCaps.tinyCaps("Upgrade wisely..."), player, player.getName());
        this.table = new ArcaneTableManager(super.getInventory());
    }

    @Override
    public void initInventory() {
        super.empty();

        super.setRow(MenuItems.BLANK_GRAY.build(), 1, 7);
        super.setRow(MenuItems.BLANK_GRAY.build(), 19, 25);
        super.setItems(MenuItems.BLANK_GRAY.build(), 9, 10, 12, 14, 16, 17);
        super.setItems(MenuItems.BLANK_ORANGE.build(), 0, 8, 9, 17, 18, 26);

        super.setItem(ArcaneTableItems.MATERIALS_SLOT.build(), MATERIAL_SLOT - 9);
        super.setItem(ArcaneTableItems.BASE_SLOT.build(), BASE_SLOT - 9);
        super.setItem(ArcaneTableItems.UPGRADE_SLOT.build(), UPGRADE_SLOT - 9);

        super.setItem(ArcaneTableItems.FORGE.build(), 22);
    }

    @EventHandler
    public void click(@NotNull InventoryClickEvent event) {
        if (!event.getInventory().equals(this.inventory)) return;

        var clickedItem = new ItemBuilder(event.getCurrentItem());

        var currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType().isAir() || clickedItem.getID() == null) return;

        var id = clickedItem.getID();
        if (id.equalsIgnoreCase("blank") || id.equalsIgnoreCase("info") || id.equalsIgnoreCase("forge")) {
            if (id.equalsIgnoreCase("forge")) this.table.forge();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void open(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().equals(super.getInventory()))) return;
        super.getPlayer().playSound(Sound.sound(Key.key("block.enchantment_table.use"), Sound.Source.BLOCK, 1.0F, 0.5F));
        super.getPlayer().playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.BLOCK, 1.0F, 0.75F));
    }

    @EventHandler
    public void close(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().equals(super.getInventory()))) return;
        super.getPlayer().playSound(Sound.sound(Key.key("block.ender_chest.close"), Sound.Source.BLOCK, 1.0F, 0.75F));

        ItemStack[] items = {
                event.getInventory().getItem(ArcaneTableMenu.BASE_SLOT),
                event.getInventory().getItem(ArcaneTableMenu.UPGRADE_SLOT),
                event.getInventory().getItem(ArcaneTableMenu.MATERIAL_SLOT)
        };

        for (var item : items) {
            if (item == null || item.getType().isAir()) continue;
            super.getPlayer().getInventory().addItem(item);
        }

        HandlerList.unregisterAll(this);
    }
}
