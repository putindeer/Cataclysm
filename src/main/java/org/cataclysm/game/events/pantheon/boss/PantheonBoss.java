package org.cataclysm.game.events.pantheon.boss;

import net.kyori.adventure.bossbar.BossBar;
import org.cataclysm.api.boss.CataclysmBoss;

public abstract class PantheonBoss extends CataclysmBoss {
    public PantheonBoss(String name, int health) {
        super(name, health);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void registerSoundtrack() {
    }

    @Override
    public void registerAbilities() {
    }

    @Override
    public void tick() {
    }

    @Override
    public BossBar buildBossBar() {
        return null;
    }
}
