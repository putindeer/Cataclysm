package org.cataclysm.game.pantheon.level.entrance;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class EntranceGUI extends BasicMenu {
    public EntranceGUI(Player player) {
        super(45, TinyCaps.tinyCaps("   • Pantheon of Cataclysm •"), player, player.getName());
    }

    @Override
    public void initInventory() {
        super.empty();

        boolean isReady = PlayerHandler.isReady(super.player);
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

        World world = super.player.getWorld();
        boolean isReady = PlayerHandler.isReady(super.player);
        if (id.equals("ready") && !isReady) {
            PlayerHandler.setReady(super.player, true);
            world.playSound(player, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1.25F);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 5.0F, 0.75F);
            world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        }
        else if (id.equals("unready") && isReady) {
            PlayerHandler.setReady(super.player, false);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, 15.0F, 0.75F);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, 15.0F, 0.65F);
        }

        Cataclysm.getPantheon().getPantheonPhaseHandler().tryElapseWaitroom();
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
        World world = super.player.getWorld();
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
    }
}
