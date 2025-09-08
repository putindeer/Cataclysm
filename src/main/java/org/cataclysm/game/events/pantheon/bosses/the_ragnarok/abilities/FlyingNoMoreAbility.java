package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

public class FlyingNoMoreAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;

    public FlyingNoMoreAbility(TheRagnarok ragnarok) {
        super(Material.ELYTRA, "Flying No More", 1);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        World world = ragnarok.getArena().center().getWorld();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isGliding()) {
                world.strikeLightning(player.getLocation());
                world.createExplosion(player.getLocation(), 6F, false, false);
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 2F, 0.6F);
            }
        }
    }
}
