package org.cataclysm.game.player.survival.resurrect.mortality;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Enemy;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.PlayerUtils;
import org.cataclysm.game.player.survival.resurrect.totems.events.PlayerUseTotemEvent;
import org.cataclysm.global.utils.chat.ChatMessenger;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Registrable
public class MortalityListener implements Listener {

    @EventHandler
    private void onPlayerUseTotem(PlayerUseTotemEvent event) {
        var player = event.getPlayer();
        var inventory = player.getInventory();
        var itemInMainHand = inventory.getItemInMainHand();
        var itemInOffHand = inventory.getItemInOffHand();
        var day = Cataclysm.getDay();
        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
        var mortalityManager = cataclysmPlayer.getMortalityManager();
        float newValue = getNewValue(event, mortalityManager, day);
        mortalityManager.setValue(newValue);

        var mortality = mortalityManager.getValue();
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.handlePlayerEffects(player, mortality);
            this.applyEnemyEffects(player, mortality);
            this.applyHealthEffects(player, mortality);
        }, 1);

        var totem = itemInMainHand;
        if (totem.getType() != Material.TOTEM_OF_UNDYING) totem = itemInOffHand;

        var builder = new ItemBuilder(totem);
        var stack = builder.item;
        var totemDisplay = stack.getItemMeta().displayName();
        if (stack.getItemMeta() == null || totemDisplay == null) totemDisplay = Component.text("tótem");
        var mortalityPercentage = mortalityManager.getPercentage();
        var totemManager = cataclysmPlayer.getTotemManager();
        var poppedTotems = totemManager.getPoppedTotems();
        Component cause = event.getCause();

        for (Player onlinePlayers : Bukkit.getOnlinePlayers()) {
            ChatMessenger.sendMessage(onlinePlayers, MiniMessage.miniMessage().deserialize(player.getName() + " activó un ").append(totemDisplay).append(Component.text(".")));
            onlinePlayers.sendMessage(MiniMessage.miniMessage().deserialize("<#6a6a6a>(N°" + poppedTotems + ". Causa: ").append(cause).append(MiniMessage.miniMessage().deserialize(". " + mortalityPercentage + ")")));
            onlinePlayers.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.MASTER, 1.0F, 0.55F));
        }

    }

    private static float getNewValue(PlayerUseTotemEvent event, MortalityManager mortalityManager, int day) {
        float currentValue = mortalityManager.getValue();
        float decreaseValue = day < 21 ? 0.010f : day < 28 ? 0.020f : 0.1f;
        var totemId = event.getTotemId();

        if (totemId != null) {
            switch (totemId) {
                case "arcane_totem" -> {
                    decreaseValue = 0.0f;
                    if (day >= 14) decreaseValue = 0.005f;
                    if (day >= 21) decreaseValue = 0.010f;
                    if (day >= 28) decreaseValue = 0.05f;
                }

                case "calamity_totem" -> {
                    decreaseValue = 0.0f;
                    if (day >= 21) decreaseValue = 0.005f;
                    if (day >= 28) decreaseValue = 0.02f;
                }
            }
        }

        if (Cataclysm.getRagnarok() != null ) {
            var level = Cataclysm.getRagnarok().getData().getLevel();
            if (level >= 3 && level < 5) decreaseValue *= 2;
            else if (level >= 5) decreaseValue *= 3;
        }

        float newValue = currentValue - decreaseValue;
        DecimalFormat df = new DecimalFormat("#.####");
        // Round to 3 decimal places to eliminate floating point errors
        newValue = Float.parseFloat(df.format(newValue));
        return newValue;
    }

    private void handlePlayerEffects(Player player, float mortality) {
        Map<Float, Runnable> effects = new TreeMap<>(Collections.reverseOrder());

        effects.put(0.98f, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 400, 1)));
        effects.put(0.95f, () -> player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE));
        effects.put(0.93f, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 400, 1)));
        effects.put(0.90f, () -> player.removePotionEffect(PotionEffectType.ABSORPTION));
        effects.put(0.80f, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 400, 1)));
        effects.put(0.75f, () -> player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0)));
        effects.put(0.60f, () -> player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 200, 0)));
        effects.put(0.50f, () -> player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 200, 0)));
        effects.put(0.25f, () -> player.removePotionEffect(PotionEffectType.REGENERATION));

        effects.forEach((threshold, action) -> {
            if (mortality <= threshold) action.run();
        });
    }

    private void applyEnemyEffects(Player player, float mortality) {
        if (mortality > 0.86f) return;

        for (var entity : player.getNearbyEntities(5, 5, 5)) {
            if (!(entity instanceof Enemy enemy)) continue;

            enemy.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));

            if (mortality <= 0.83f) {
                enemy.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            }
        }
    }

    private void applyHealthEffects(Player player, float mortality) {
        if (mortality <= 0.1f) {
            PlayerUtils.operateMaxHealth(player, -1);
        }

        if (mortality <= 0.01f) {
            PlayerUtils.setMaxHealth(player, 1);
        }
    }

}
