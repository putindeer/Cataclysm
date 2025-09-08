package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

@Registrable
public class FightChatHandler implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null || !(pantheon.getBoss() instanceof TheCataclysm cataclysm)) return;

        if (!cataclysm.isChat()) {
            event.setCancelled(true);
        }

        if (cataclysm.isVulnerable()) {
            for (Player target : Bukkit.getOnlinePlayers()) {
                if (event.getMessage().equalsIgnoreCase(target.getName())) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4));
                }
            }
        }
    }

}
