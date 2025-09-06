package org.cataclysm.game.events.pantheon.bosses.void_lord.moves;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;

@Getter @Setter
public abstract class HeartAbility extends PantheonAbility {
    private boolean voidLord;

    public HeartAbility(Material triggerMaterial, String name, int channelTime) {
        super(triggerMaterial, name, channelTime);
    }

    @Override
    public void channel() {

    }

    @Override
    public void cast() {

    }
}
