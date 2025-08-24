package org.cataclysm.game.effect;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.mob.custom.dungeon.temple.Paragon;

@Registrable
public class DisperEffect implements Listener {
    public static final PotionEffectType EFFECT_TYPE = PotionEffectType.UNLUCK;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || !player.hasPotionEffect(EFFECT_TYPE)) return;

        event.setCancelled(true);
        player.playSound(Sound.sound(Key.key("block.conduit.activate"), Sound.Source.BLOCK, 1.0F, 1.85F));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || !player.hasPotionEffect(EFFECT_TYPE)) return;

        var type = event.getBlock().getType();
        if (type.equals(Paragon.CORE_BLOCK_TYPE)
                || type.equals(Material.CREAKING_HEART)
                || type.equals(Material.SPAWNER)
                || type.equals(Material.NETHERITE_BLOCK)
                || type.equals(Material.DIAMOND_BLOCK)
                || type.equals(Material.GOLD_BLOCK)
                || type.equals(Material.EMERALD_BLOCK)
                || type.equals(Material.IRON_BLOCK)
                || type.equals(Material.DEEPSLATE_DIAMOND_ORE)
                || type.equals(Material.ANCIENT_DEBRIS)
                || type.equals(Material.PLAYER_HEAD)
                || type.equals(Material.PIGLIN_HEAD)
                || type.equals(Material.WITHER_SKELETON_SKULL)
                || type.equals(Material.SKELETON_SKULL)
                || type.equals(Material.COBWEB)
        ) return;

        event.setCancelled(true);
        player.playSound(Sound.sound(Key.key("block.conduit.activate"), Sound.Source.BLOCK, 1.0F, 1.45F));
    }

    @EventHandler
    public void onWaterPlace(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE || !player.hasPotionEffect(EFFECT_TYPE)) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getItem() == null || event.getItem().getType() != Material.WATER_BUCKET) return;

        event.setCancelled(true);
        player.playSound(Sound.sound(Key.key("block.conduit.activate"), Sound.Source.BLOCK, 1.0F, 1.85F));
    }
}
