package org.cataclysm.game.effect;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.game.player.PlayerUtils;
import org.cataclysm.global.utils.chat.ChatMessenger;

@Registrable
public class MortemEffect implements Listener {
    public static final PotionEffectType EFFECT_TYPE = PotionEffectType.WIND_CHARGED;

    @EventHandler(priority = EventPriority.HIGH)
    private void entityResurrectEvent(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (PlayerUtils.hasArmor(ItemFamily.PALE_ARMOR, player)) return;
        if (PlayerUtils.hasMirageHelmet(player)) return;
        PlayerInventory inventory = player.getInventory();

        ItemStack itemInMainHand = inventory.getItemInMainHand();
        ItemStack itemInOffHand = inventory.getItemInOffHand();

        if (!(itemInMainHand.getType().equals(Material.TOTEM_OF_UNDYING)) && !(itemInOffHand.getType().equals(Material.TOTEM_OF_UNDYING))) return;

        var totem = itemInMainHand;
        if (totem.getType() != Material.TOTEM_OF_UNDYING) totem = itemInOffHand;

        var builder = new ItemBuilder(totem);
        var id = builder.getID();
        if (!player.hasPotionEffect(MortemEffect.EFFECT_TYPE) || (id != null && id.contains("paragon"))) return;

        for (var onlinePlayers : Bukkit.getOnlinePlayers()) {
            ChatMessenger.sendMessage(onlinePlayers, player.getName() + " intentó usar un tótem con " + ChatMessenger.getCataclysmColor() + "MORTEM" + ChatMessenger.getTextColor() + ".");
            onlinePlayers.playSound(Sound.sound(Key.key("item.trident.thunder"), Sound.Source.MASTER, 1.0F, 1.75F));
        }

        event.setCancelled(true);
    }
}
