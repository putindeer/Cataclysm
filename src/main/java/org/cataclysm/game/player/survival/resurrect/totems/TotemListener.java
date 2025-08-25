package org.cataclysm.game.player.survival.resurrect.totems;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.survival.resurrect.totems.events.PlayerUseTotemEvent;

@Registrable
public class TotemListener implements Listener {

    @EventHandler
    private void entityResurrectEvent(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var inventory = player.getInventory();
        var itemInMainHand = inventory.getItemInMainHand();
        var itemInOffHand = inventory.getItemInOffHand();

        if (!(itemInMainHand.getType().equals(Material.TOTEM_OF_UNDYING)) && !(itemInOffHand.getType().equals(Material.TOTEM_OF_UNDYING))) return;

        var totem = itemInMainHand;
        if (totem.getType() != Material.TOTEM_OF_UNDYING) totem = itemInOffHand;

        var builder = new ItemBuilder(totem);
        var id = builder.getID();

        if (player.hasPotionEffect(MortemEffect.EFFECT_TYPE) && id != null && !id.contains("paragon")) return;

        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
        TotemManager totemManager = cataclysmPlayer.getTotemManager();

        totemManager.addPoppedTotem();

        Component cause = MiniMessage.miniMessage().deserialize("No disponible");

        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause != null) cause = TotemUtils.formatDamageCause(lastDamageCause);

        var mortalityManager = cataclysmPlayer.getMortalityManager();
        var mortalityPercentage = mortalityManager.getPercentage();

        var poppedTotems = totemManager.getPoppedTotems();

        new PlayerUseTotemEvent(player, cause, id, poppedTotems, mortalityPercentage).callEvent();

        Location location = player.getLocation();
        Bukkit.getConsoleSender().sendMessage(player.getName() + " used a totem. " + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
    }

    //TODO DIOS ME SALVE
    public Component replaceTextInComponent(Component original, String target, String replacement) {
        return original.replaceText(builder -> builder.match(target).replacement(replacement));
    }
}