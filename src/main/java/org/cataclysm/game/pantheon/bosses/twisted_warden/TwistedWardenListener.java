package org.cataclysm.game.pantheon.bosses.twisted_warden;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.boss.events.BossFightStopEvent;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.pantheon.bosses.twisted_warden.abilities.NightmareAbility;

public class TwistedWardenListener implements Listener {

    @EventHandler
    private void onBossChannelAbility(BossChannelAbilityEvent event) {
        var boss = event.getBoss();
        var controller = boss.getController();

        var hasNightmare = NightmareAbility.hasNightmare(controller);
        if (!hasNightmare) return;

        var ability = event.getAbility();
        if (ability.getTrigger().getType() != Material.ECHO_SHARD) return;

        controller.setCooldown(Material.ECHO_SHARD, 5);
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        var nightmare = NightmareAbility.hasNightmare(player);

        if (!nightmare || !(player.getPotionEffect(PotionEffectType.GLOWING) == null)) return;

        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (NightmareAbility.DURATION * 20), 0));
        player.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 2F, 0.52F);
        player.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2F, 0.5F);
        player.playSound(player.getLocation(), Sound.ENTITY_WARDEN_ROAR, 4F, 0.5F);
    }

    @EventHandler
    public void onBossfightStop(BossFightStopEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            var incursionUpgrade = PersistentData.get(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER);
            PersistentData.set(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER, incursionUpgrade != null ? incursionUpgrade + 1 : 1);
        });
        HandlerList.unregisterAll(this);
    }
}
