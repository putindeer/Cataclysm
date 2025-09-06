package org.cataclysm.game.events.pantheon.bosses.twisted_warden;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.twisted_warden.abilities.NightmarePantheonAbility;
import org.cataclysm.game.events.raids.bosses.twisted_warden.abilities.NightmareAbility;
import org.cataclysm.global.utils.chat.ChatMessenger;

@Registrable
public class PantheonWardenListener implements Listener {

    @EventHandler
    private void onBossChannelAbility(BossChannelAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof PantheonBoss boss)) return;

        Player controller = boss.getController();

        boolean inNightmare = NightmareAbility.hasNightmare(controller);
        if (!inNightmare) return;

        if (event.getAbility().getTrigger().getType() == Material.ECHO_SHARD) {
            controller.setCooldown(Material.ECHO_SHARD, 5);
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null
                || pantheon.getBoss() == null
                || !(pantheon.getBoss() instanceof PantheonWarden warden)
                || player == warden.getController()) return;

        boolean hasNightmare = NightmarePantheonAbility.hasNightmare(player);
        if (!hasNightmare) return;

        String prefix = ChatMessenger.wrapPrefix("<#3E2270>nightmare");
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "<#575757>¡La pesadilla de <#bfbfbf>" + player.getName() + " <#575757>ha comenzado!"));
            all.playSound(all, Sound.BLOCK_ENDER_CHEST_CLOSE, 2F, 0.5F);
            all.playSound(all, Sound.ENTITY_SKELETON_HORSE_DEATH, 2F, 0.5F);
        }

        NightmarePantheonAbility.applyNightmareEffects(player);
        NightmarePantheonAbility.setNightmare(player, false);
    }

}
