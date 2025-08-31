package org.cataclysm.game.events.raids.bosses.twisted_warden.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.AbilityUltimate;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.events.raids.bosses.twisted_warden.TwistedWarden;
import org.cataclysm.game.events.raids.bosses.twisted_warden.keys.TwistedWardenKeys;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class NightmareAbility extends AbilityUltimate {
    public static final int DURATION = 10;

    private final TwistedWarden warden;

    public NightmareAbility(TwistedWarden warden) {
        super(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, "Nightmare", 4, 120);
        this.warden = warden;
    }

    @Override
    public void channel() {
        for (var player : this.warden.getFighters()) {
            Sound[] sounds = {Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT};
            for (var sound : sounds) player.playSound(player, sound, 1.2F, 0.55F);

            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0));
        }
    }

    @Override
    public void cast() {
        this.setUp(true);
        this.warden.getController().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (DURATION * 20), 0));
        this.warden.getThread().getService().schedule(() -> this.setUp(false), DURATION, TimeUnit.SECONDS);
    }

    private void setUp(boolean activate) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            if (activate) this.warden.getSoundtrack().stopAll();
            else this.warden.getSoundtrack().loop("THEME", 224);
        });

        this.warden.setAbilityVisibility(!activate);
        this.toggleGlobalNightmare(activate);
    }

    private void toggleGlobalNightmare(boolean nightmare) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            var arena = this.warden.getArena();
            var fighters = arena.getPlayersInArena();
            for (var fighter : fighters) this.setNightmare(fighter, nightmare);

            var controller = this.warden.getController();
            this.setNightmare(controller, nightmare);
        });
    }

    public void setNightmare(@NotNull Player player, boolean nightmare) {
        if (nightmare) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, DURATION, 0));
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 1.2F, 0.75F);
        }
        PersistentData.set(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN, nightmare);
    }

    public static boolean hasNightmare(@NotNull Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN));
    }
}
