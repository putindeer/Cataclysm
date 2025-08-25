package org.cataclysm.game.mob.custom.dungeon.temple;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitTask;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.game.mob.utils.MobUtils;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Paragon extends CataclysmMob {
    public static final Material CORE_BLOCK_TYPE = Material.NETHERITE_BLOCK;
    public static final int DISPER_RADIUS = 15;

    private final transient Mob mob;

    private transient Location coreLocation;
    private transient BukkitTask task;

    public Paragon(Level level) {
        super(new ParagonEntity(level), "Paragon", level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setListener(new ParagonListener(this));

        super.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.LIGHT).buildAsNMS());
        super.amplifyAttribute(Attributes.SCALE, 1.4);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 100);
        super.setAttribute(Attributes.FOLLOW_RANGE, DISPER_RADIUS);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0);
        super.setCollidable(false);

        this.target(null);
        this.mob = (Mob) super.getEntity();

        super.setPregenerationMaterial(Material.WHITE_GLAZED_TERRACOTTA);
    }

    public void setCoreBlock(@NotNull Location coreLocation) {
        this.coreLocation = coreLocation;
        this.startTickTask();
    }

    private void startTickTask() {
        var structure = super.getStructure();
        if (structure == null) return;

        this.task = Bukkit.getScheduler().runTaskTimer(Cataclysm.getInstance(), () -> {
            var livingEntity = super.getBukkitLivingEntity();
            if (this.mob.getTarget() != null) {
                var speedAmplifier = livingEntity.getLocation().distance(this.coreLocation);
                super.setAttribute(Attributes.MOVEMENT_SPEED, this.getBaseSpeed() + (speedAmplifier / 275));
            }
        }, 0, 20L);
    }

    public void warp() {
        Location location = this.getBukkitLivingEntity().getLocation();
        this.warpParticle(location);
        location.getWorld().playSound(location, "entity.breeze.inhale", SoundCategory.HOSTILE, 1.5F, 0.5F);
        for (int i = 0; i < 8; i++) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                this.coreLocation.getWorld().playSound(this.coreLocation, "item.trident.return", SoundCategory.HOSTILE, 1.5F, 0.5F);
                new ParticleHandler(this.coreLocation).circle(2, Particle.END_ROD);
            }, i * 5);
        }

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            if (this.mob.getTarget() != null) return;
            location.getWorld().playSound(location, "item.trident.riptide_3", SoundCategory.HOSTILE, 1F, 0.5F);
            this.getBukkitLivingEntity().teleport(this.coreLocation.clone().add(0, 1, 0));
            this.coreLocation.getWorld().playSound(this.coreLocation, "item.trident.thunder", SoundCategory.HOSTILE, 1.5F, 0.75F);
            this.warpParticle(this.coreLocation);
        }, 40);
    }

    private void warpParticle(@NotNull Location location) {
        for (int i = 0; i < 6; i++) {
            new ParticleHandler(location.clone().add(0, i * 0.5, 0)).circle(3, Particle.END_ROD);
        }
    }

    public void death() {
        this.getBukkitLivingEntity().damage(10000000);

        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }

        if (this.coreLocation == null) return;

        var orb = this.coreLocation.getWorld().spawn(this.coreLocation, ExperienceOrb.class);
        orb.setExperience(10);
        this.coreLocation.getWorld().spawnEntity(this.coreLocation, org.bukkit.entity.EntityType.EXPERIENCE_ORB);

        this.coreLocation.getWorld().stopSound(SoundStop.named(Key.key("block.beacon.ambient")));
        this.coreLocation.getWorld().playSound(this.coreLocation, "block.beacon.deactivate", SoundCategory.BLOCKS, 1F, 0.65F);
        this.coreLocation.getWorld().playSound(this.coreLocation, "entity.creaking.deactivate", SoundCategory.BLOCKS, 1F, 0.65F);
    }

    public void awaking() {
        this.coreLocation.getWorld().playSound(this.coreLocation, "entity.iron_golem.death", SoundCategory.BLOCKS, 1.5F, 0.55F);
        this.coreLocation.getWorld().playSound(this.coreLocation, "entity.ravager.stunned", SoundCategory.BLOCKS, 2F, 0.55F);
        this.setAttribute(Attributes.MOVEMENT_SPEED, getBaseSpeed());
    }

    public void curse(org.bukkit.entity.@NotNull Player targetPlayer) {
        targetPlayer.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), Sound.Source.HOSTILE, 1.0F, 0.75F));
        targetPlayer.playSound(Sound.sound(Key.key("entity.elder_guardian.curse"), Sound.Source.HOSTILE, 1.0F, 1.35F));
    }

    public void hurt(@NotNull EntityDamageEvent event) {
        LivingEntity livingEntity = super.getBukkitLivingEntity();
        livingEntity.getWorld().playSound(Sound.sound(Key.key("entity.iron_golem.repair"), Sound.Source.HOSTILE, 1.0F, 0.85F), livingEntity);
        event.setCancelled(true);
    }

    protected void target(@Nullable Player player) {
        if (this.getBukkitLivingEntity() == null || !this.getBukkitLivingEntity().isValid()) return;

        Mob mob = (Mob) this.getEntity();
        mob.setTarget(null, EntityTargetEvent.TargetReason.CUSTOM);
        if (player != null) mob.setTarget(player, EntityTargetEvent.TargetReason.CUSTOM);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Paragon(super.getLevel());
    }

    static class ParagonEntity extends IronGolem {
        public ParagonEntity(Level level) {
            super(EntityType.IRON_GOLEM, level);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, false));
            this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
            this.goalSelector.addGoal(2, new MoveBackToVillageGoal(this, 0.6, false));
            this.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(this, 0.6));
            this.goalSelector.addGoal(5, new OfferFlowerGoal(this));
            this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
            this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
            this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
            this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
        }
    }

    static class ParagonListener implements Listener {
        private final Paragon paragon;

        public ParagonListener(Paragon paragon) {
            this.paragon = paragon;
        }

        @EventHandler
        public void onAttack(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof LivingEntity damager)) return;

            CataclysmToken token = CataclysmMob.getToken(damager);
            if (token == null || !token.key().equals(this.paragon.getMobToken().key())) return;

            if (!(event.getEntity() instanceof org.bukkit.entity.Player player)) return;
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 10, 0)), 1);
        }

        @EventHandler
        private void onChangeTarget(EntityTargetLivingEntityEvent event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null) return;

            if (!token.key().equals(this.paragon.getMobToken().key())) return;

            if (event.getTarget() instanceof org.bukkit.entity.Player player) {
                this.paragon.setCollidable(true);
                this.paragon.curse(player);
                this.paragon.awaking();
            }

            if (event.getTarget() == null) {
                this.paragon.setCollidable(false);
                this.paragon.setAttribute(Attributes.MOVEMENT_SPEED, 0);
                this.paragon.warp();
            }
        }

        @EventHandler
        private void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.paragon.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onCoreBlockBreak(BlockBreakEvent event) {
            Location location = event.getBlock().getLocation();
            if (location.equals(this.paragon.coreLocation.getBlock().getLocation())) {
                this.paragon.death();
                event.setDropItems(false);
                location.getWorld().dropItem(location, CataclysmItems.PARAGON_QUARTZ.build());
                if (Cataclysm.getEventManager() != null) location.getWorld().dropItem(location, CataclysmItems.PARAGON_QUARTZ.build());

                var player = event.getPlayer();
                new CataclysmAdvancement("the_beginning/one_shot_survivor").grant(player);
            }
        }

        @EventHandler
        public void onSpawn(EntitySpawnEvent event) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

                CataclysmToken token = CataclysmMob.getToken(livingEntity);
                if (token == null || !token.key().equals(this.paragon.getMobToken().key())) return;

                Location location = MobUtils.getNearestBlock(livingEntity.getLocation(), CORE_BLOCK_TYPE, DISPER_RADIUS);
                if (location != null) {
                    this.paragon.setCoreBlock(location);
                    if (location.getWorld() == Dimensions.PALE_VOID.getWorld()) {
                        livingEntity.customName(MiniMessage.miniMessage().deserialize("<" + ItemFamily.PALE_ARMOR.getColor() + ">Pale Paragon"));
                    }
                } else Bukkit.getConsoleSender().sendMessage("Paragon location was null");
                if (Cataclysm.getDay() >= 7) MobUtils.speedBoost(livingEntity, 1);
            }, 60L);
        }

        @EventHandler
        private void onDamage(EntityDamageEvent event) {
            if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.paragon.getMobToken().key())) return;

            if (event.getDamage() >= 10000000) return;

            this.paragon.hurt(event);
        }
    }

    public double getBaseSpeed() {
        float velocity = 0.25F;
        if (Cataclysm.getDay() >= 7) velocity += 0.10F;
        if (this.getBukkitLivingEntity().getWorld() == Dimensions.PALE_VOID.getWorld()) velocity *= 2;
        return velocity;
    }
}