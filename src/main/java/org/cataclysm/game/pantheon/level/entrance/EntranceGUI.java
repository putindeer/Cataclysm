package org.cataclysm.game.pantheon.level.entrance;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.pantheon.PantheonUtils;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class EntranceGUI extends BasicMenu {
    public EntranceGUI(Player player) {
        super(45, TinyCaps.tinyCaps("   • Pantheon of Cataclysm •"), player, player.getName());
    }

    @Override
    public void initInventory() {
        super.empty();

        boolean isReady = PantheonUtils.isReady(super.player);
        ItemStack item = createReadyItem();
        if (isReady) item = createUnreadyItem();

        super.setItem(item, 22);
    }

    @EventHandler
    public void click(@NotNull InventoryClickEvent event) {
        if (!event.getInventory().equals(super.inventory)) return;
        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || item.getType().isAir() || !event.isLeftClick()) return;

        var id = new ItemBuilder(item).getID();
        if (id == null || id.equals("blank")) return;

        boolean isReady = PantheonUtils.isReady(super.player);
        if (id.equals("ready") && !isReady) {
            PantheonUtils.setReady(super.player, true);
            super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 0.55F));
            super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 0.65F));
            super.player.playSound(Sound.sound(Key.key("item.trident.return"), Sound.Source.BLOCK, 5.0F, 0.75F));
        } else if (id.equals("unready") && isReady) {
            PantheonUtils.setReady(super.player, false);
            super.player.playSound(Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.BLOCK, 1.0F, 0.75F));
        }

        super.close();
    }

    private ItemStack createReadyItem() {
        return new ItemBuilder(Material.OPEN_EYEBLOSSOM).setDisplay(TinyCaps.tinyCaps("preparado")).setID("ready").build();
    }
    private ItemStack createUnreadyItem() {
        return new ItemBuilder(Material.CLOSED_EYEBLOSSOM).setDisplay(TinyCaps.tinyCaps("no listo")).setID("unready").build();
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
