package org.cataclysm.game.events.pantheon.orchestrator.fountain.gui;

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
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;
import org.cataclysm.global.utils.text.font.TinyCaps;

public class FountainGUI extends BasicMenu {
    private PantheonProfile profile;

    public FountainGUI(Player player) {
        super(45, TinyCaps.tinyCaps("   • Pantheon of Cataclysm •"), player, player.getName());
    }

    @Override
    public void initInventory() {
        this.profile = PantheonProfile.fromPlayer(Cataclysm.getPantheon(), super.player);

        ItemStack item = createReadyItem();
        if (profile.isReady()) item = createUnreadyItem();
        super.setItem(item, 22);
    }

    @EventHandler
    public void click(InventoryClickEvent event) {
        if (!event.getInventory().equals(super.inventory)) return;
        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || item.getType().isAir() || !event.isLeftClick()) return;

        var id = new ItemBuilder(item).getID();
        if (id == null || id.equals("blank")) return;

        World world = super.player.getWorld();
        if (id.equals("ready") && !profile.isReady()) {
            profile.setReady(true);
            world.playSound(player, org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 5.0F, 1.25F);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_ACTIVATE, 5.0F, 0.75F);
            world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        }
        else if (id.equals("unready") && profile.isReady()) {
            profile.setReady(false);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, 15.0F, 0.75F);
            world.playSound(player, org.bukkit.Sound.BLOCK_BEACON_DEACTIVATE, 15.0F, 0.65F);
        }


        Cataclysm.getPantheon().getFountain().getStatusNotifier().update();
        super.close();
    }

    private ItemStack createReadyItem() {
        return new ItemBuilder(Material.OPEN_EYEBLOSSOM).setDisplay(TinyCaps.tinyCaps("preparado")).setID("ready").build();
    }
    private ItemStack createUnreadyItem() {
        return new ItemBuilder(Material.CLOSED_EYEBLOSSOM).setDisplay(TinyCaps.tinyCaps("no listo")).setID("unready").build();
    }

    @EventHandler
    public void open(InventoryOpenEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;
        World world = super.player.getWorld();
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
        world.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RETURN, 15.0F, 0.65F);
    }

    public static void setStatus() {

    }
}
