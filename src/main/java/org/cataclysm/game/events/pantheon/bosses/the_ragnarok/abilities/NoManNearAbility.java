package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

public class NoManNearAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;

    public NoManNearAbility(TheRagnarok ragnarok) {
        super(Material.BELL, "No Man Near", 2);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        Location center = ragnarok.getArena().center();
        World world = center.getWorld();

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.getWorld().equals(world)) continue;

            double distance = player.getLocation().distance(center);
            if (distance < 12) {
                player.damage(80);
                Vector knock = player.getLocation().toVector().subtract(center.toVector()).normalize().multiply(3);
                player.setVelocity(knock);
            }
        }
        world.createExplosion(center, 4F, false, false);
    }
}
