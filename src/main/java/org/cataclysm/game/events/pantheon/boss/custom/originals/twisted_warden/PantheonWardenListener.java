package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities.NightmarePantheonAbility;
import org.cataclysm.global.utils.chat.ChatMessenger;

public class PantheonWardenListener implements Listener {

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null || !(pantheon.getBoss() instanceof PantheonWarden)) return;

        var player = event.getPlayer();
        var nightmare = NightmarePantheonAbility.hasNightmare(player);

        if (!nightmare || player.hasPotionEffect(PotionEffectType.GLOWING)) return;

        NightmarePantheonAbility.castNightmareEffects(player);

        String prefix = ChatMessenger.wrapPrefix("<#3E2270>nightmare");
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "¡La pesadilla de <#3E2270>" + player.getName() + " ha comenzado!"));
            all.playSound(all, Sound.BLOCK_ENDER_CHEST_CLOSE, 2F, 0.5F);
            all.playSound(all, Sound.ENTITY_SKELETON_HORSE_DEATH, 2F, 0.5F);
        }
    }

}
