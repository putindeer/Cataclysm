package org.cataclysm.game.events.pantheon.boss.twisted_warden.abilities;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
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
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.PantheonWarden;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.keys.PantheonWardenKeys;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NightmarePantheonAbility extends PantheonAbility {
    private static final @Getter int SECONDS = 10;
    private static final @Getter int TICKS = (SECONDS * 20);

    private final PantheonWarden warden;

    public NightmarePantheonAbility(PantheonWarden warden) {
        super(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, "Nightmare", "<#3E2270>", 4);
        this.warden = warden;
    }

    @Override
    public void channel() {
        for (var player : Bukkit.getOnlinePlayers()) {
            Sound[] sounds = {Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT};
            for (var sound : sounds) player.playSound(player, sound, 1.2F, 0.55F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0));
        }
    }

    @Override
    public void cast() {
        this.setUp(true);
        this.warden.getController().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, TICKS, 0));
        this.warden.getThread().getService().schedule(() -> this.setUp(false), SECONDS, TimeUnit.SECONDS);
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
            setNightmare(this.warden.getController(), nightmare);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode().isInvulnerable()) continue;
                setNightmare(player, nightmare);
            }
        });
    }

    public static void setNightmare(@NotNull Player player, boolean nightmare) {
        if (nightmare) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, TICKS, 0));
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 5F, 0.75F);
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 5F, 0.5F);
            player.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 5F, 0.5F);
        }
        PersistentData.set(player, PantheonWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN, nightmare);
    }

    public static boolean hasNightmare(@NotNull Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, PantheonWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN));
    }

    public static List<Sound> sounds = List.of(
            Sound.ENTITY_BLAZE_DEATH,
            Sound.ENTITY_ENDER_DRAGON_GROWL,
            Sound.ENTITY_ELDER_GUARDIAN_CURSE,
            Sound.ENTITY_WARDEN_ROAR
    );

    public static void castNightmareEffects(@NotNull Player player) {
        player.showTitle(
                Title.title(MiniMessage.miniMessage().deserialize("<#3E2270><obf>Nightmare"),
                MiniMessage.miniMessage().deserialize("<#504A52><i>ɪ ꜱᴇᴇ ʏᴏᴜ..."),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4000), Duration.ofMillis(2000))));
        for (int i = 0; i < SECONDS * 2; i++) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                Sound sound = sounds.get(new Random().nextInt(0, sounds.size()));
                player.playSound(player, sound, 2F, (float) new Random().nextDouble(.5, .75));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0));
            }, 10 * i);
        }
        setNightmare(player, false);
    }
}
