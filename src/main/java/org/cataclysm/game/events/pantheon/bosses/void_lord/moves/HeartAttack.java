package org.cataclysm.game.events.pantheon.bosses.void_lord.moves;

import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;

public abstract class HeartAttack extends Ability {
    public final VoidLord lord;

    public HeartAttack(VoidLord lord, Material triggerMaterial, String name, int channelTime, double cooldown) {
        super(triggerMaterial, name, channelTime, cooldown, false);
        this.lord = lord;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {}
}
