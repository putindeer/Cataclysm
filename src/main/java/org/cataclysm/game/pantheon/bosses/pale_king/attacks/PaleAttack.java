package org.cataclysm.game.pantheon.bosses.pale_king.attacks;

import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.pantheon.bosses.pale_king.PaleKing;

public abstract class PaleAttack extends Ability {
    public final PaleKing king;

    public PaleAttack(PaleKing king, Material triggerMaterial, String name, int channelTime, double cooldown) {
        super(triggerMaterial, name, channelTime, cooldown, false);
        this.king = king;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {}
}
