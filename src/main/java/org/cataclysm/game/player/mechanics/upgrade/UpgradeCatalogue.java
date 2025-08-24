package org.cataclysm.game.player.mechanics.upgrade;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.inventory.BasicMenu;
import org.cataclysm.api.inventory.MenuItems;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.data.PlayerLoader;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

public class UpgradeCatalogue extends BasicMenu {
    private final boolean cataclysmUpgrade;

    public UpgradeCatalogue(Player player, boolean cataclysmUpgrade) {
        super(54, getDisplay(player), player, player.getName());
        this.cataclysmUpgrade = cataclysmUpgrade;
    }

    @Override
    public void initInventory() {
        super.empty();

        super.setOutline(MenuItems.BLANK.build());
        super.setItems(MenuItems.BLANK.build(), 0, 1, 7, 8, 9, 10, 16, 17, 18, 19, 25, 26, 27, 28, 34, 35, 36, 37, 43, 44);

        for (var upgrade : Upgrades.values()) super.setItem(upgrade.buildItemStack(this.player), upgrade.getCatalogueSlot());
    }

    @EventHandler
    public void click(@NotNull InventoryClickEvent event) {
        if (!event.getInventory().equals(super.inventory)) return;
        event.setCancelled(true);

        var item = event.getCurrentItem();
        if (item == null || item.getType().isAir() || !event.isLeftClick()) return;

        var id = new ItemBuilder(item).getID();
        if (id == null || id.equals("blank")) return;

        var cp = CataclysmPlayer.getCataclysmPlayer(super.player);
        if (cp == null) return;

        var um = cp.getUpgradeManager();
        Upgrades upgrade;
        try {
            upgrade = Upgrades.valueOf(id.toUpperCase());
        } catch (IllegalArgumentException e) {
            return;
        }

        if (!this.cataclysmUpgrade) {
            super.player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 1.05F));
            return;
        }

        var definitions = upgrade.getDefinition();
        if (definitions == null) return;

        var currentLevel = um.getUpgradeLevel(upgrade);
        var maxLevel = definitions.levels().length;

        if (currentLevel >= maxLevel) {
            super.player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 1.35F));
            return;
        }

        um.addUpgradeLevel(upgrade);
        super.player.playSound(Sound.sound(Key.key("block.respawn_anchor.charge"), Sound.Source.BLOCK, 1.0F, 0.85F));
        super.player.playSound(Sound.sound(Key.key("block.beacon.power_select"), Sound.Source.BLOCK, 1.0F, 0.85F));
        super.player.playSound(Sound.sound(Key.key("ui.button.click"), Sound.Source.BLOCK, 1.0F, 1.25F));

        var offHanditem = super.player.getInventory().getItemInOffHand();
        offHanditem.setAmount(offHanditem.getAmount() - 1);

        try {
            new PlayerLoader(cp.getData().getNickname()).save(cp);
        } catch (Exception e) {
            throw new RuntimeException("Unable to save player data", e);
        }

        super.close();
    }

    @EventHandler
    public void open(@NotNull InventoryOpenEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;

        super.player.playSound(Sound.sound(Key.key("ui.cartography_table.take_result"), Sound.Source.BLOCK, 1.0F, 1.25F));
        super.player.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.BLOCK, 0.6F, 2F));

        if (this.cataclysmUpgrade) {
            super.player.playSound(Sound.sound(Key.key("block.anvil.use"), Sound.Source.BLOCK, 1.0F, 1.75F));
        }
    }

    @EventHandler
    public void close(@NotNull InventoryCloseEvent event) {
        if (!(event.getInventory().equals(super.inventory))) return;

        super.player.playSound(Sound.sound(Key.key("ui.cartography_table.take_result"), Sound.Source.BLOCK, 1.0F, 1.65F));
        super.player.playSound(Sound.sound(Key.key("block.shulker_box.close"), Sound.Source.BLOCK, 0.6F, 2F));
    }

    private static @NotNull String getDisplay(Player player) {
        var incursionUpgrade = PersistentData.get(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER);
        int finalIncursions = 0;
        if (incursionUpgrade != null) finalIncursions = incursionUpgrade;
        return TinyCaps.tinyCaps("Mejoras: ") + CataclysmPlayer.getCataclysmPlayer(player).getUpgradeManager().getUpgrades() + "/" + (Upgrades.getWeekUpgrades() + finalIncursions);
    }
}
