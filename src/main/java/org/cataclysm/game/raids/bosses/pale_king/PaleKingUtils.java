package org.cataclysm.game.raids.bosses.pale_king;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class PaleKingUtils {

    public static void breakElytras(@NotNull Player player, int cooldown) {
        PlayerInventory inventory = player.getInventory();

        ItemStack chestplate = inventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.ELYTRA) return;

        ItemStack elytra = chestplate.clone();

        Item item = player.getWorld().dropItemNaturally(player.getLocation(), elytra);
        item.setInvulnerable(true);
        item.setGlowing(true);
        item.setPickupDelay(0);

        chestplate.setAmount(0);

        if (cooldown != 0) player.setCooldown(Material.ELYTRA, cooldown);
        player.playSound(player, Sound.ITEM_SHIELD_BREAK, 1, 1);
    }

}
