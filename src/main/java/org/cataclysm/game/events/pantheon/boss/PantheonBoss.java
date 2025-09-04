package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.BossUtils;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.events.BossFightEndEvent;
import org.cataclysm.api.boss.events.BossFightStartEvent;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.player.PlayerUtils;

@Setter
@Getter
public abstract class PantheonBoss extends CataclysmBoss {
    private boolean invulnerable = false;

    private PantheonOfCataclysm pantheon;

    public PantheonBoss(String name, int health) {
        super(name, health);
    }

    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(getController())) return;
        livingEntity.damage(amount);
        livingEntity.setNoDamageTicks(25);
    }

    public void startPantheonFight() {
        super.setUpController(true);

        if (this.listener != null) Bukkit.getPluginManager().registerEvents(this.listener, Cataclysm.getInstance());
        new BossFightStartEvent(this).callEvent();

        this.onStart();
        this.thread.startTickTask();
        this.pantheon.setBoss(this);
    }

    public void stopPantheonFight() {
        this.thread.getService().shutdownNow();
        this.soundtrack.stopAll();

        super.setUpBossBar(false);
        super.setUpController(false);
        super.removeModel();

        new BossFightEndEvent(this).callEvent();

        this.onStop();
        this.pantheon.setBoss(null);
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
