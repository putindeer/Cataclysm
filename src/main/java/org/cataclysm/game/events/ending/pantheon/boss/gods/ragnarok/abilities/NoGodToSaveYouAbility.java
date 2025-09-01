package org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.abilities;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.TheRagnarok;

import java.time.Duration;

public class NoGodToSaveYouAbility extends RagnarokAbility {
    public NoGodToSaveYouAbility(TheRagnarok ragnarok) {
        super(ragnarok, Material.TURTLE_SCUTE, "No God To Save You", 1);
    }

    @Override
    public void channel() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_BLAZE_DEATH, 10F, .52F);
        });
    }

    @Override
    public void cast() {
        Material[] materials = {Material.TURTLE_SCUTE, Material.ENDER_PEARL};
        Bukkit.getOnlinePlayers().forEach(player -> {
            for (Material material : materials) player.setCooldown(material, 600);
            for (int i = 0; i < 5; i++) {
                int finalI = i;
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    player.getWorld().playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 5F, (float) (1.0 - (finalI * .2)));
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 5F, (float) (1.0 - (finalI * .2)));
                }, i * 2);
            }
            player.showTitle(
                    Title.title(
                            MiniMessage.miniMessage().deserialize("<" + CataclysmColor.PARAGON.getColor() + "><obf>El fin se acerca de caca"),
                            MiniMessage.miniMessage().deserialize("<gradient:" + CataclysmColor.PARAGON.getColor() + ":" + CataclysmColor.PARAGON.getColor3() + ">" + "You Feel Immortal?</gradient>"),
                            Title.Times.times(Duration.ofSeconds(3), Duration.ofSeconds(5), Duration.ofSeconds(2))
                    )
            );
        });
    }
}
