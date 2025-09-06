package org.cataclysm.game.events.pantheon.bosses.void_lord.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.api.boss.events.BossCastAbilityEvent;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAbility;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAttack;
import org.jetbrains.annotations.NotNull;

@Registrable
public class VoidLordListener implements Listener {
    private static final PantheonOfCataclysm PANTHEON = Cataclysm.getPantheon();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (PANTHEON != null && PANTHEON.getBoss() instanceof VoidLord lord)
            handleDamage(lord);
    }

    private static void handleDamage(@NotNull VoidLord lord) {
        Location location = lord.getController().getLocation();
        location.getWorld().playSound(location, Sound.BLOCK_CREAKING_HEART_BREAK, 2F, .75F);
        lord.handleEvents();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onControllerAttack(EntityDamageByEntityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null || !(pantheon.getBoss() instanceof VoidLord lord)) return;

        lord.slash();
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onControllerInteract(PlayerInteractEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null || !(pantheon.getBoss() instanceof VoidLord lord)) return;

        Player controller = lord.getController();
        if (!(event.getPlayer().equals(controller)) || !event.getAction().isLeftClick()) return;

        lord.slash();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBossCastAbility(BossCastAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof VoidLord lord)) return;

        Ability ability = event.getAbility().clone();
        if (ability instanceof HeartAttack) return;

        HeartAbility heartAbility = (HeartAbility) ability;
        if (heartAbility.isBoosted()) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                heartAbility.setBoosted(false);
                lord.setBoosted(false);
            }, 20);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onBossChannelAbility(BossChannelAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof VoidLord lord)) return;

        Ability ability = event.getAbility().clone();
        if (ability instanceof HeartAttack) return;

        HeartAbility heartAbility = (HeartAbility) ability;

        if (lord.getCurrentPhase() == 2) {
            heartAbility.setVoidLord(true);
            heartAbility.setChannelTime(1);
        }

        pantheon.getDispatcher().sendMessage("El jefe usará la habilidad " + heartAbility.getHoverName());
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.BLOCK_END_PORTAL_FRAME_FILL, 7.0F, 0.65F);
            player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 7.0F, 0.65F);
            player.playSound(player, Sound.BLOCK_ENDER_CHEST_OPEN, 2.0F, 0.65F);
            if (heartAbility.getTitle() != null) player.showTitle(heartAbility.getTitle());
        }
    }
}
