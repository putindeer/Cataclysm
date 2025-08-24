package org.cataclysm.api.item;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.inventory.MenuItems;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class ItemCatalogue extends BasicMenu {
    private int page = 0;

    public ItemCatalogue(Player player) {super(54, "", player, player.getName());}

    @Override
    public void initInventory() {
        super.empty();

        super.setOutline(MenuItems.BLANK.build());
        super.setItem(MenuItems.PREVIOUS.build(), 45);
        super.setItem(MenuItems.NEXT.build(), 53);

        for (var i = 0; i < 28; i++) {
            final var itemList = CataclysmItems.getList();

            final var index = ((28 * this.page) + i);

            if (index >= itemList.size()) break;

            final var item = itemList.get(index);
            super.addItem(item.build());
        }
    }

    @EventHandler
    public void click(@NotNull InventoryClickEvent event) {
        var clickedInventory = event.getClickedInventory();
        if (clickedInventory == null || clickedInventory != this.inventory) return;

        event.setCancelled(true);

        final var currentItem = event.getCurrentItem();
        final var clickedItem = new ItemBuilder(event.getCurrentItem());

        if (currentItem == null || currentItem.getType().isAir()) return;
        if (clickedItem.getID().equals("blank") || clickedItem.getID() == null) return;
        if (!event.isLeftClick()) return;

        switch (clickedItem.getID()) {
            case "next" -> {
                final var itemsList = CataclysmItems.getList();
                final var totalPages = Math.floor((double) itemsList.size()/28);
                if (totalPages == this.page) return;
                this.page++;
                this.initInventory();
            }
            case "previous" -> {
                if (this.page == 0) return;
                this.page--;
                this.initInventory();
            }
            default -> {
                player.sendMessage("Item getted");
                player.getInventory().addItem(clickedItem.build());
                player.playSound(Sound.sound(Key.key("ui.loom.select_pattern"), Sound.Source.MASTER, 1.0F, 1.25F));
            }
        }

        player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.MASTER, 1.0F, 1.25F));
    }

    @EventHandler
    public void open(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().equals(this.inventory))) return;
        player.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.MASTER, 1.0F, 1.25F));
    }

    @EventHandler
    public void close(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;
        super.player.playSound(Sound.sound(Key.key("block.shulker_box.close"), Sound.Source.MASTER, 1.0F, 1.25F));
        HandlerList.unregisterAll(this);
    }
}