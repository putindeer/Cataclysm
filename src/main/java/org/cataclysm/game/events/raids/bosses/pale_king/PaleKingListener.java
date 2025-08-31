package org.cataclysm.game.events.raids.bosses.pale_king;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cataclysm.Cataclysm;

public class PaleKingListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onControllerAttack(EntityDamageByEntityEvent event) {
        if (!(Cataclysm.getBoss() instanceof PaleKing king)) return;

        if (!(event.getDamager().equals(king.getController()))) return;
        king.castSlash();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onControllerInteract(PlayerInteractEvent event) {
        if (!(Cataclysm.getBoss() instanceof PaleKing king)) return;

        Player controller = king.getController();
        if (!(event.getPlayer().equals(controller)) || !event.getAction().isLeftClick()) return;

        king.castSlash();
    }

}
