package org.cataclysm.game.raids.bosses.pale_king;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.raids.bosses.twisted_warden.abilities.NightmareAbility;

public class PaleKingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onControllerAttack(EntityDamageByEntityEvent event) {
        if (!(Cataclysm.getBossFight() instanceof PaleKing king)) return;

        if (!(event.getDamager().equals(king.getController()))) return;
        king.castSlash();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onControllerInteract(PlayerInteractEvent event) {
        if (!(Cataclysm.getBossFight() instanceof PaleKing king)) return;

        Player controller = king.getController();
        if (!(event.getPlayer().equals(controller)) || !event.getAction().isLeftClick()) return;

        king.castSlash();
    }

}
