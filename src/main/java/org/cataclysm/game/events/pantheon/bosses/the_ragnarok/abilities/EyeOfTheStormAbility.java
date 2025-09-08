package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

public class EyeOfTheStormAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;

    public EyeOfTheStormAbility(TheRagnarok ragnarok) {
        super(Material.ENDER_EYE, "Eye of The Storm", 5);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        Location center = ragnarok.getArena().center();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(center.getWorld())) continue;

            double dist = player.getLocation().distance(center);
            if (dist > 15) { // fuera del ojo
                player.setHealth(0.0);
            } else {
                // efecto visual de ser absorbido
                Vector pull = center.toVector().subtract(player.getLocation().toVector()).normalize().multiply(2);
                player.setVelocity(pull);
            }
        }
    }
}
