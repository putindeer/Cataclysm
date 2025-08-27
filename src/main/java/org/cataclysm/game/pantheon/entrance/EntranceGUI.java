package org.cataclysm.game.pantheon.entrance;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.inventory.MenuItems;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class EntranceGUI extends BasicMenu {
    public EntranceGUI(Player player) {
        super(54, TinyCaps.tinyCaps("   • Pantheon of Cataclysm •"), player, player.getName());
    }

    @Override
    public void initInventory() {
        super.empty();

        super.setOutline(MenuItems.BLANK.build());
        super.setItems(MenuItems.BLANK.build(), 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 34, 35, 36, 37, 43, 44);
    }

    @EventHandler
    public void click(@NotNull InventoryClickEvent event) {
        if (!event.getInventory().equals(super.inventory)) return;
        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || item.getType().isAir() || !event.isLeftClick()) return;

        var id = new ItemBuilder(item).getID();
        if (id == null || id.equals("blank")) return;

        switch(id) {}

        super.close();
    }

    @EventHandler
    public void open(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;
        super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 1.25F));
        super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 1.55F));
        super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 0.65F));
    }

    @EventHandler
    public void close(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;
        super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 1.0F, 1.95F));
    }
}
