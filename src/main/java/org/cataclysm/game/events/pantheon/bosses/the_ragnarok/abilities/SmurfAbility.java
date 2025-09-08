package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

public class SmurfAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;

    public SmurfAbility(TheRagnarok ragnarok) {
        super(Material.BLUE_DYE, "Smurf", 2);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {
        // opcional: alguna animación previa
    }

    @Override
    public void cast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 20, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 20, 200)); // hack visual "enano"
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 20, 1));
        }
        ragnarok.getController().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 20, 2));
    }
}
