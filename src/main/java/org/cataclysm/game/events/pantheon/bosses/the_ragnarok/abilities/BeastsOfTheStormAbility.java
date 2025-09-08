package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

public class BeastsOfTheStormAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;

    public BeastsOfTheStormAbility(TheRagnarok ragnarok) {
        super(Material.PHANTOM_MEMBRANE, "Beasts of The Storm", 3);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        World world = ragnarok.getArena().center().getWorld();
        for (int i = 0; i < 10; i++) {
            world.spawnEntity(ragnarok.getArena().getRandomLocations(1).getFirst(), EntityType.PHANTOM);
        }
    }
}
