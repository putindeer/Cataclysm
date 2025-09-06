package org.cataclysm.game.events.pantheon.bosses.void_lord;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities.BlasarosaHeartAbility;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities.EmbraceTheVoidAbility;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities.KingsoulAbility;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities.TerracismaAbility;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks.HeartDashAttack;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks.HeartScourageAttack;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.attacks.HeartTeleportAttack;
import org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator.VoidLordOrchestrator;
import org.cataclysm.game.events.pantheon.bosses.void_lord.utils.BossBarFormatter;
import org.cataclysm.game.items.CataclysmItems;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class VoidLord extends PantheonBoss {
    private String modelPrefix;
    private boolean elapsing;
    private int currentPhase;

    private final ItemStack sword;

    private final VoidLordConfigurator configurator;
    private final VoidLordOrchestrator orchestrator;
    private final ScheduledExecutorService executor;

    public VoidLord() {
        super(7500);
        super.arena = new CataclysmArea(PantheonLevels.PALE_HEART.getLocation(), 50);
        this.configurator = new VoidLordConfigurator(this);
        this.orchestrator = new VoidLordOrchestrator(this);
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.sword = CataclysmItems.PALE_SWORD.build();
    }


    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(super.controller)) return;

        livingEntity.damage(amount);
        livingEntity.setNoDamageTicks(40);

        if (livingEntity instanceof Player player && player.isBlocking()) {
            player.setCooldown(Material.SHIELD, (int) (amount * 10));
            player.playSound(player, Sound.ITEM_SHIELD_BREAK, 2F, .55F);
        }
    }

    public void slash() {
        Material swordType = this.sword.getType();
        if (super.controller.hasCooldown(swordType) || !super.controller.getInventory().getItemInMainHand().getType().equals(swordType)) return;

        Location start = super.controller.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        Vector offset = direction.clone().multiply(3);
        Location location = start.clone().add(offset);

        double radius = 2;

        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SWEEP_ATTACK, radius + 1, radius * 6);

        location.getNearbyLivingEntities(radius, radius, radius).forEach(livingEntity -> {
            this.damage(livingEntity, 30);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 3F, 1.75F);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 50, 1));
        });

        this.playSound(Sound.ITEM_TRIDENT_RETURN, 3F, 1.75F);
        this.playSound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3F, 1.75F);
        this.playSound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3F, 1.25F);

        super.controller.setCooldown(swordType, 5);
    }

    public void createExplosion(Location location, double radius) {
        location = location.clone().add(0, (radius / 1.5), 0);
        double warnRadius = (radius / 3);

        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.END_ROD, warnRadius, (int) (warnRadius * 4));

        super.controller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 30, 0, false, false));
        super.controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 30, 9, false, false));
        this.playSound(Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 4F, .66F);

        World world = location.getWorld();
        Location finalLocation = location;
        this.thread.getService().schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                finalLocation.getNearbyLivingEntities(radius).forEach(livingEntity ->
                        this.damage(livingEntity, 30 * radius));
                handler.sphere(Particle.END_ROD, radius, radius * 8);
                handler.sphere(Particle.EXPLOSION_EMITTER, (warnRadius / 2), ((warnRadius / 2) * 6));
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_THUNDER, 4F, .56F);
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_THUNDER, 4F, .66F);
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_RIPTIDE_2, 4F, .86F);
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_RIPTIDE_2, 4F, .76F);
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
                world.playSound(finalLocation, Sound.ITEM_TRIDENT_RETURN, 4F, 1.6F);
            });
        }, 1, TimeUnit.SECONDS);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        Location location = super.controller.getLocation();
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void handleEvents() {
        if (this.elapsing && this.health >= this.maxHealth) this.orchestrator.startPhase(this.currentPhase + 1);
        if (!this.elapsing && this.health <= 0) this.orchestrator.startTrial(this.currentPhase);
    }

    public void handleBossBar(int value) {
        super.getBossBar().name(BossBarFormatter.formatBarName(value));
        if (value > 2) super.bossBar.color(BossBar.Color.PINK);
    }

    @Override
    public void onStart() {this.configurator.applySetUp(true);}

    @Override
    public void onStop() {this.configurator.applySetUp(false);}

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new EmbraceTheVoidAbility(this));
        super.abilityManager.addAbility(new KingsoulAbility(this));
        super.abilityManager.addAbility(new BlasarosaHeartAbility(this));
        super.abilityManager.addAbility(new TerracismaAbility(this));
        super.abilityManager.addAbility(new HeartDashAttack(this));
        super.abilityManager.addAbility(new HeartScourageAttack(this));
        super.abilityManager.addAbility(new HeartTeleportAttack(this));
    }

    @Override
    public void registerSoundtrack() {VoidLordThemes.registerTracks(this.soundtrack);}

    @Override
    public BossBar buildBossBar() {return BossBarFormatter.buildBossBar();}
}
