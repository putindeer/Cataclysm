package org.cataclysm.game.raids.bosses.pale_king;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

public class PaleKingUtils {

    public static void breakElytras(Player player, int cooldown) {
        PlayerInventory inventory = player.getInventory();

        ItemStack chestplate = inventory.getChestplate();
        if (chestplate == null || chestplate.getType() != Material.ELYTRA) return;

        ItemStack elytra = chestplate.clone();
        inventory.addItem(elytra);

        chestplate.setAmount(0);

        player.setCooldown(Material.ELYTRA, cooldown);
        player.playSound(player, Sound.ITEM_SHIELD_BREAK, 1, 1);
    }

}
