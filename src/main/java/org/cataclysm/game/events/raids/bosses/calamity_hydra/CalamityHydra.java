package org.cataclysm.game.events.raids.bosses.calamity_hydra;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ravager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.HydrazerAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.rage.AtomicBreathAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.HellquakeAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.HydraBreathAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.rage.CalamityAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.rage.MeteorShowerAbility;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.attacks.CalamityExplosion;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.rage.HydraRage;
import org.cataclysm.game.events.raids.structures.RaidStructures;

public class CalamityHydra extends CataclysmBoss {
    public HydraPhase phase;
    public HydraRage rage;
    public int heads;

    public CalamityHydra(String name, int health) {
        super(name, health);
        try {
            super.arena = RaidStructures.MOTHER.getStructure().getBossArena();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        super.listener = new HydraListener();
        this.phase = new HydraPhase(this);
        this.rage = new HydraRage(this);
    }

    @Override
    public void onStart() {
        BossBar rageBar = this.rage.getManager().createRageBar();
        super.getArena().getPlayersInArena().forEach(rageBar::addViewer);

        this.setUpAttributes(true);
        this.phase.start(1);
    }

    @Override
    public void onStop() {
        BossBar rageBar = this.rage.getRageBar();
        super.getArena().getPlayersInArena().forEach(rageBar::removeViewer);

        this.setUpAttributes(false);
        this.phase.stop();
    }

    @Override
    public void registerSoundtrack() {
        super.soundtrack.addTrack("PHASE_1", Key.key("cataclysm.boss.calamity_hydra.theme.phase_1"));
        super.soundtrack.addTrack("PHASE_2", Key.key("cataclysm.boss.calamity_hydra.theme.phase_2"));
        super.soundtrack.addTrack("PHASE_3", Key.key("cataclysm.boss.calamity_hydra.theme.phase_3"));
    }

    @Override
    public void registerAbilities() {
        //super.abilityManager.addAbility(new CometImpactAbility(this));
        super.abilityManager.addAbility(new MeteorShowerAbility(this));
        super.abilityManager.addAbility(new AtomicBreathAbility(this));
        super.abilityManager.addAbility(new CalamityAbility(this));

        super.abilityManager.addAbility(new HellquakeAbility(this));
        super.abilityManager.addAbility(new HydraBreathAbility(this));
        super.abilityManager.addAbility(new HydrazerAbility(this));
    }

    @Override
    public void tick() {
        double scale = this.getAttribute(Attribute.SCALE);
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            this.phase.tryElapse();
            this.getLocation().getNearbyLivingEntities(scale).forEach(livingEntity -> {
                if (livingEntity.equals(this.controller) || livingEntity instanceof Ravager) return;
                livingEntity.setFireTicks(100);
                livingEntity.damage(35, this.getController());
            });
        });
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("<\uE667>"),
                1.0F,
                BossBar.Color.YELLOW,
                BossBar.Overlay.NOTCHED_6);
    }

    public void summonAreaEffectCloud(Location location, float radius, int amplifier) {
        AreaEffectCloud effectCloud = (AreaEffectCloud) location.getWorld().spawnEntity(location, EntityType.AREA_EFFECT_CLOUD);
        effectCloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 200, amplifier), true);
        effectCloud.setSource(this.controller);
        effectCloud.setColor(Color.ORANGE);
        effectCloud.setDuration(150);
        effectCloud.setRadius(radius);
    }

    public void toggleVisibility(boolean visibility) {
        if (visibility) super.controller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, 0));
        else super.controller.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public void createHydraExplosion(Location location, double power, CalamityExplosion.Type type) {
        new CalamityExplosion(this).create(location, power, type);
    }

    public void setUpAttributes( boolean cast) {
        if (cast) {
            double scale = 7.0;
            this.setAttribute(Attribute.SCALE, scale);
            this.setAttribute(Attribute.ENTITY_INTERACTION_RANGE, scale * 3);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 2);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.175);
            this.setAttribute(Attribute.STEP_HEIGHT, 4);
            this.setAttribute(Attribute.JUMP_STRENGTH, 1);
        } else {
            this.resetAttribute(Attribute.SCALE);
            this.resetAttribute(Attribute.ENTITY_INTERACTION_RANGE);
            this.resetAttribute(Attribute.KNOCKBACK_RESISTANCE);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.1);
            this.resetAttribute(Attribute.STEP_HEIGHT);
            this.resetAttribute(Attribute.JUMP_STRENGTH);
        }
    }

    public Location getLocation() {
        return super.controller.getLocation();
    }

    public void playSound(Sound sound, float volume, float pitch) {
        this.getLocation().getWorld().playSound(this.getLocation(), sound, volume, pitch);
    }

    public void resetAttribute(Attribute attribute) {
        AttributeInstance instance = super.controller.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(instance.getDefaultValue());
    }

    public void setAttribute(Attribute attribute, double baseValue) {
        AttributeInstance instance = super.controller.getAttribute(attribute);
        if (instance != null) instance.setBaseValue(baseValue);
    }

    public double getAttribute(Attribute attribute) {
        AttributeInstance instance = super.controller.getAttribute(attribute);
        if (instance != null) return instance.getValue();
        return 0.0;
    }
}
