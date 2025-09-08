package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

import java.time.Duration;

public class NoGodToSaveYouAbility extends PantheonAbility {

    private static final int BLINDNESS_TICKS = 40;
    private static final int COOLDOWN_TICKS = 600;
    private static final int SOUND_REPETITIONS = 5;
    private static final int SOUND_INTERVAL_TICKS = 2;
    private static final Material[] COOLDOWN_ITEMS = {Material.TURTLE_SCUTE, Material.ENDER_PEARL};
    private final TheCataclysm cataclysm;

    public NoGodToSaveYouAbility(TheCataclysm cataclysm) {
        super(Material.TURTLE_SCUTE, "No God To Save You", 1);
        this.cataclysm = cataclysm;
    }

    @Override
    public void channel() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            var world = player.getWorld();
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, BLINDNESS_TICKS, 0));
            world.playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10F, 0.52F);
        });
    }

    @Override
    public void cast() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            var world = player.getWorld();

            // Aplicar cooldown a los items
            for (Material material : COOLDOWN_ITEMS) player.setCooldown(material, COOLDOWN_TICKS);

            // Reproducir sonidos escalonados
            for (int i = 0; i < SOUND_REPETITIONS; i++) {
                int finalI = i;
                scheduleSync(() -> {
                    float pitch = 1.0F - (finalI * 0.2F);
                    world.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 5F, pitch);
                    world.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 5F, pitch);
                }, i * SOUND_INTERVAL_TICKS);
            }

            // Mostrar título dramático
            player.showTitle(
                    Title.title(
                            MiniMessage.miniMessage().deserialize(
                                    "<" + CataclysmColor.PARAGON.getColor() + "><obf>The End Approaches</obf>"
                            ),
                            MiniMessage.miniMessage().deserialize(
                                    "<gradient:" + CataclysmColor.PARAGON.getColor() + ":" + CataclysmColor.PARAGON.getColor3() + ">You Feel Immortal?</gradient>"
                            ),
                            Title.Times.times(Duration.ofSeconds(3), Duration.ofSeconds(5), Duration.ofSeconds(2))
                    )
            );
        });
    }

    /** Ejecuta un Runnable en el hilo principal de Bukkit */
    private void scheduleSync(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), task, delayTicks);
    }
}