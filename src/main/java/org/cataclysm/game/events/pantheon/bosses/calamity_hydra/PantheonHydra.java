package org.cataclysm.game.events.pantheon.bosses.calamity_hydra;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ravager;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.abilities.*;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.rage.PantheonRage;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public class PantheonHydra extends PantheonBoss {
    private boolean elapsing = false;
    private boolean outraged = false;
    private double resistance = 1.0;
    private int heads;
    private int phase;

    public final PantheonHydraEvents eventManager;
    public final PantheonRage rage;

    public PantheonHydra() {
        super("Calamity Dragon", 20000);
        super.arena = new CataclysmArea(PantheonLevels.HYDRAS_DUNGEON.getLocation(), 170);
        this.eventManager = new PantheonHydraEvents(this);
        this.rage = new PantheonRage(this);
    }

    public void ignite(float radius, double damage, int fireTicks) {
        Location location = super.controller.getLocation().clone().add(0, 1, 0);
        location.getNearbyLivingEntities(radius, e -> !e.equals(controller) && !(e instanceof Ravager))
                .forEach(e -> {
                    e.setFireTicks(fireTicks);
                    e.damage(damage, getController());
                    e.getWorld().playSound(e.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 0.8F);
                });
    }

    public void changeHeads(int headsLeft, int totalHeads) {
        this.updateModel(headsLeft + "/" + totalHeads);
        this.setHeads(headsLeft);
    }

    public void handleEvents() {
        if (!this.elapsing) this.eventManager.handlePhaseElapse(this.phase, super.health);
        if (this.phase == 3) this.eventManager.handleHeadDecapitation(this.heads, 5, super.health);
        if (!this.rage.isFreezed()) this.rage.infurate(5);
        this.ignite(10, 50, 100);
    }

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new MeteorShowerPantheonAbility(this));
        super.abilityManager.addAbility(new AtomicBreathPantheonAbility(this));
        super.abilityManager.addAbility(new HellquakePantheonAbility(this));
        super.abilityManager.addAbility(new HydraBreathPantheonAbility(this));
        super.abilityManager.addAbility(new HydrazerPantheonAbility(this));
    }

    @Override
    public void registerSoundtrack() {
        super.soundtrack.addTrack("PHASE_1", Key.key("cataclysm.boss.calamity_hydra.phase_1"));
        super.soundtrack.addTrack("PHASE_2", Key.key("cataclysm.boss.calamity_hydra.phase_2"));
        super.soundtrack.addTrack("PHASE_3", Key.key("cataclysm.boss.calamity_hydra.phase_3"));
    }

    public void handleBossbars(boolean activate) {
        if (activate) super.setUpBossBar(true);
        BossBar rageBar = this.rage.getBarManager().getBossBar();
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (activate) rageBar.addViewer(p);
            else rageBar.removeViewer(p);
        });
    }

    public void handleControllerAttributes(boolean cast) {
        if (cast) {
            this.setAttribute(Attribute.SCALE, 8);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 2);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.185);
            this.setAttribute(Attribute.STEP_HEIGHT, 4);
            this.setAttribute(Attribute.JUMP_STRENGTH, 1);
        }
        else {
            this.resetAttribute(Attribute.SCALE);
            this.resetAttribute(Attribute.KNOCKBACK_RESISTANCE);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.1);
            this.resetAttribute(Attribute.STEP_HEIGHT);
            this.resetAttribute(Attribute.JUMP_STRENGTH);
        }
    }

    public void updateBarName() {
        super.getBossBar().name(this.getBarName());
    }

    public void updateModel(String value) {
        super.updateModel(EntityType.RAVAGER, "cd-" + value);
    }

    private @NotNull Component getBarName() {
        String fire = "<#b8976a>\uD83D\uDD25".toUpperCase();
        String obf = "<#ab8559><obf>||</obf>".toUpperCase();
        String name = TinyCaps.tinyCaps(super.getName().toLowerCase());
        return MiniMessage.miniMessage().deserialize(fire + " " + obf + " <gradient:#a8512f:#b07c4c>" + name + " " + obf + " " + fire);
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                this.getBarName(),
                1.0F,
                BossBar.Color.RED,
                BossBar.Overlay.NOTCHED_6);
    }

    @Override
    public void onStart() {
        handleControllerAttributes(true);
        handleBossbars(true);
    }

    @Override
    public void onStop() {
        handleControllerAttributes(false);
        handleBossbars(false);
    }

    @Override
    public void tick() {Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::handleEvents);}
}
