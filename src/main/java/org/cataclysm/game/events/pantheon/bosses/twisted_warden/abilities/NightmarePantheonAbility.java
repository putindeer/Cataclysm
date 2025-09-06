package org.cataclysm.game.events.pantheon.bosses.twisted_warden.abilities;

import lombok.Getter;
import net.kyori.adventure.text.format.NamedTextColor;
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
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.twisted_warden.PantheonWarden;
import org.cataclysm.game.events.raids.bosses.twisted_warden.keys.TwistedWardenKeys;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class NightmarePantheonAbility extends PantheonAbility {
    private static final @Getter int SECONDS = 10;
    private static final @Getter int TICKS = (SECONDS * 20);

    private final PantheonWarden warden;

    public NightmarePantheonAbility(PantheonWarden warden) {
        super(Material.SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, "Nightmare", "#3E2270", 4);
        super.setTitle(Title.title(
                MiniMessage.miniMessage().deserialize("<" + getColor() + ">" + getName()),
                MiniMessage.miniMessage().deserialize("<#6a6070>ʏᴏᴜ ᴄᴀɴ'ᴛ ꜱᴇᴇ ɴᴏᴛʜɪɴɢ"),
                Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(3), Duration.ofSeconds(1))
        ));
        this.warden = warden;
    }

    @Override
    public void channel() {
        Sound[] sounds = {Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT};
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (Sound sound : sounds) player.playSound(player, sound, 1.2F, 0.55F);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 80, 0));
        });
    }

    @Override
    public void cast() {
        this.setUp(true);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.setUp(false), TICKS);
    }

    private void setUp(boolean activate) {
        this.warden.setAbilityVisibility(!activate);
        if (activate) {
            MobUtils.setGlowingColor(this.warden.getController(), NamedTextColor.DARK_PURPLE);
            this.warden.getSoundtrack().stopAll();
        }
        else {
            MobUtils.removeGlowingEffect(this.warden.getController());
            this.warden.getSoundtrack().loop("THEME", 224);
        }
        Bukkit.getOnlinePlayers().forEach(player -> setNightmare(player, activate));
        this.warden.getController().removePotionEffect(PotionEffectType.BLINDNESS);
    }

    public static void setNightmare(@NotNull Player player, boolean nightmare) {
        if (player.getGameMode().isInvulnerable()) return;
        if (nightmare) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, TICKS, 0));
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 5F, 0.75F);
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 5F, 0.65F);
            player.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_DEATH, 5F, 0.55F);
        }
        PersistentData.set(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN, nightmare);
    }

    public static boolean hasNightmare(@NotNull Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN));
    }

    public static void applyNightmareEffects(@NotNull Player player) {
        List<Sound> sounds = List.of(
                Sound.ENTITY_BLAZE_DEATH,
                Sound.ENTITY_ENDER_DRAGON_GROWL,
                Sound.ENTITY_ELDER_GUARDIAN_CURSE,
                Sound.ENTITY_WARDEN_ROAR
        );

        player.showTitle(
                Title.title(MiniMessage.miniMessage().deserialize("<#3E2270><obf>Nightmare"),
                MiniMessage.miniMessage().deserialize("<#504A52><i>ɪ ꜱᴇᴇ ʏᴏᴜ..."),
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(4), Duration.ofMillis(2))));

        for (int i = 0; i < SECONDS * 2; i++) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                Sound sound = sounds.get(new Random().nextInt(0, sounds.size()));
                player.playSound(player, sound, 2F, (float) new Random().nextDouble(.5, .75));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 20, 0));
            }, 10 * i);
        }
    }
}
