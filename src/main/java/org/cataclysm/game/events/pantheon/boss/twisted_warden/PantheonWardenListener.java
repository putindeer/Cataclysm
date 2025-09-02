package org.cataclysm.game.events.pantheon.boss.twisted_warden;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.boss.events.BossFightEndEvent;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.abilities.NightmarePantheonAbility;
import org.cataclysm.global.utils.chat.ChatMessenger;

import java.time.Duration;

public class PantheonWardenListener implements Listener {
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        var player = event.getPlayer();
        var nightmare = NightmarePantheonAbility.hasNightmare(player);

        if (!nightmare || !(player.getPotionEffect(PotionEffectType.GLOWING) == null)) return;

        NightmarePantheonAbility.castNightmareEffects(player);

        String prefix = ChatMessenger.wrapPrefix("<#3E2270>nightmare");
        for (Player all : Bukkit.getOnlinePlayers()) {
            all.sendMessage(MiniMessage.miniMessage().deserialize(prefix + "La pesadilla de " + player.getName() + " ha comenzado."));
            all.playSound(all, Sound.BLOCK_ENDER_CHEST_CLOSE, 2F, 0.5F);
            all.playSound(all, Sound.ENTITY_SKELETON_HORSE_DEATH, 2F, 0.5F);
        }
    }
}
