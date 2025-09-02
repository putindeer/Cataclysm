package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.events.BossFightEndEvent;
import org.cataclysm.api.boss.events.BossFightStartEvent;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;

@Setter
@Getter
public abstract class PantheonBoss extends CataclysmBoss {
    private PantheonOfCataclysm pantheon;

    public PantheonBoss(String name, int health) {
        super(name, health);
    }

    public void startPantheonFight() {
        super.setUpController(true);

        if (this.listener != null) Bukkit.getPluginManager().registerEvents(this.listener, Cataclysm.getInstance());
        this.thread.startTickTask();

        new BossFightStartEvent(this).callEvent();
        onStart();

        pantheon.setBoss(this);
    }

    public void stopPantheonFight() {
        this.thread.getService().shutdownNow();
        this.soundtrack.stopAll();

        setUpBossBar(false);
        setUpController(false);

        new BossFightEndEvent(this).callEvent();
        onStop();

        pantheon.setBoss(null);
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
