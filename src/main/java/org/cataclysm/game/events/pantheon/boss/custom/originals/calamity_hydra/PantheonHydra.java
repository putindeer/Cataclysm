package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ravager;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HellquakePantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HydraBreathPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.HydrazerPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.AtomicBreathPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities.MeteorShowerPantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks.CalamityExplosion;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage.PantheonRage;

public class PantheonHydra extends PantheonBoss {
    public PantheonHydraPhase phaseManager;
    public PantheonRage rageManager;
    public int heads;

    public PantheonHydra() {
        super("Calamity Hydra", 15000);
        super.arena = new CataclysmArea(PantheonLevels.HYDRAS_DUNGEON.getLocation(), 170);
        this.phaseManager = new PantheonHydraPhase(this);
        this.rageManager = new PantheonRage(this);
    }

    public void damage(LivingEntity livingEntity, double damage) {
        super.damage(livingEntity, damage);
        livingEntity.setFireTicks(100);
        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_DRAGON_FIREBALL_EXPLODE, 1F, 0.8F);
        livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXTINGUISH_FIRE, 1F, 0.8F);
    }

    @Override
    public void onStart() {
        BossBar rageBar = this.rageManager.getManager().createRageBar();
        super.getArena().getPlayersInArena().forEach(rageBar::addViewer);

        this.setUpAttributes(true);
        this.phaseManager.start(1);
    }

    @Override
    public void onStop() {
        BossBar rageBar = this.rageManager.getRageBar();
        super.getArena().getPlayersInArena().forEach(rageBar::removeViewer);

        this.setUpAttributes(false);
        this.phaseManager.stop();
    }

    @Override
    public void registerSoundtrack() {
        super.soundtrack.addTrack("PHASE_1", Key.key("cataclysm.boss.calamity_hydra.phase_1"));
        super.soundtrack.addTrack("PHASE_2", Key.key("cataclysm.boss.calamity_hydra.phase_2"));
        super.soundtrack.addTrack("PHASE_3", Key.key("cataclysm.boss.calamity_hydra.phase_3"));
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
    public void tick() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            this.rageManager.getManager().infuriate((3 * phaseManager.getPhase()));
            this.phaseManager.tryElapse();
            this.getLocation().getNearbyLivingEntities(8).forEach(livingEntity -> {
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
        effectCloud.setColor(Color.RED);
        effectCloud.setDuration(150);
        effectCloud.setRadius(radius);
    }

    public void createHydraExplosion(Location location, double power, CalamityExplosion.Type type) {
        new CalamityExplosion(this).create(location, power, type);
    }

    public void setUpAttributes( boolean cast) {
        if (cast) {
            this.setAttribute(Attribute.SCALE, 8);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 2);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.185);
            this.setAttribute(Attribute.STEP_HEIGHT, 4);
            this.setAttribute(Attribute.JUMP_STRENGTH, 1);
        } else {
            this.resetAttribute(Attribute.SCALE);
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
}
