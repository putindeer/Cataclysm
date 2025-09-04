package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HellquakePantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HydraBreathPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HydrazerPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.AtomicBreathPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.MeteorShowerPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage.PantheonRage;

import java.util.Collection;

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
        super("Calamity Hydra", 20000);
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
        if (this.phase == 3) this.eventManager.handleHeadDecapitation(this.heads, super.health);
        this.rage.infuriate(10);
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

    public void updateModel(String value) {
        super.updateModel(EntityType.RAVAGER, "cd-" + value);
    }

    @Override
    public BossBar buildBossBar() {
        String fire = "<#b8976a>\uD83D\uDD25".toUpperCase();
        String obf = "<#ab8559><obf>||</obf>".toUpperCase();
        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(fire + " " + obf + " <#caa207>ᴄᴀʟᴀᴍɪᴛʏ ʜʏᴅʀᴀ " + obf + " " + fire),
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
