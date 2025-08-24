package org.cataclysm.game.mob.custom.dungeon.monolith;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.SpellcasterIllager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

@Setter
@Getter
public class Trickster extends CataclysmMob {
    public static final int MAX_BUNNIES = 8;

    private int bunniesGenerated;

    public Trickster(Level level) {
        super(new TricksterEntity(level), "Trickster", level);
        ((TricksterEntity) super.getEntity()).cataclysmMob = this;
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setHealth(48);
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.GREEN_GLAZED_TERRACOTTA);
    }

    static class TricksterEntity extends Evoker {
        private Trickster cataclysmMob;

        public TricksterEntity(Level level) {
            super(EntityType.EVOKER, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            super.goalSelector.addGoal(0, new FloatGoal(this));
            super.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Player.class, 8.0F, 0.6, 1.0F));

            this.goalSelector.addGoal(4, new GnomificationSpellGoal(this));
            this.goalSelector.addGoal(4, new BunnyTrickGoal(this, 3));
            this.goalSelector.addGoal(4, new DisapearSpellGoal(this, 60));

            super.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6));
            super.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
            super.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
            super.targetSelector.addGoal(2, (new NearestAttackableTargetGoal<>(this, Player.class, true)).setUnseenMemoryTicks(300));
            super.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, false));
        }

        class BunnyTrickGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
            private final TricksterEntity entity;
            private final int amount;

            public BunnyTrickGoal(@NotNull TricksterEntity entity, int amount) {
                this.entity = entity;
                this.amount = amount;
            }

            @Override
            public void start() {
                super.start();
                var livingEntity = this.entity.getBukkitLivingEntity();
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    var amount = this.amount;
                    if (this.entity.cataclysmMob.bunniesGenerated > MAX_BUNNIES) amount = 0;
                    for (int i = 0; i < amount; i++) {
                        new TricksterKillerBunny(this.entity.level(), this.entity.cataclysmMob).addFreshEntity(livingEntity.getLocation());
                    }
                }, 15);
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.setIsCastingSpell(IllagerSpell.NONE);
            }

            @Override
            protected void performSpellCasting() {}

            @Override
            protected int getCastingTime() {
                return 30;
            }

            @Override
            protected int getCastingInterval() {
                return 300;
            }

            @Override
            protected SoundEvent getSpellPrepareSound() {
                return SoundEvents.EVOKER_PREPARE_WOLOLO;
            }

            @Override
            protected @NotNull IllagerSpell getSpell() {
                return IllagerSpell.WOLOLO;
            }
        }

        class DisapearSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
            private final TricksterEntity entity;
            private final int ticks;

            public DisapearSpellGoal(@NotNull TricksterEntity entity, int ticks) {
                this.entity = entity;
                this.ticks = ticks;
            }

            @Override
            public void start() {
                super.start();

                var livingEntity = this.entity.getBukkitLivingEntity();
                livingEntity.getWorld().playSound(livingEntity.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, SoundCategory.HOSTILE, 1.0F, 1.25F);

                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, this.ticks, 0, true, false));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, this.ticks, 2, true, false));
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.HOSTILE, 1.0F, 1.25F);
                }, 15);

                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), org.bukkit.Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, SoundCategory.HOSTILE, 1.0F, 1.55F);
                }, this.ticks);
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.setIsCastingSpell(IllagerSpell.NONE);
            }

            @Override
            protected void performSpellCasting() {}

            @Override
            protected int getCastingTime() {
                return this.ticks + 15;
            }

            @Override
            protected int getCastingInterval() {
                return 150;
            }

            @Override
            protected SoundEvent getSpellPrepareSound() {
                return SoundEvents.EVOKER_FANGS_ATTACK;
            }

            @Override
            protected @NotNull IllagerSpell getSpell() {
                return IllagerSpell.FANGS;
            }
        }

        class GnomificationSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
            private final TricksterEntity entity;
            private final HashMap<UUID, Integer> ticks;

            public GnomificationSpellGoal(@NotNull TricksterEntity entity) {
                this.entity = entity;
                this.ticks = new HashMap<>();
            }

            @Override
            public void start() {
                super.start();
                var livingEntity = this.entity.getTarget();
                if (livingEntity == null) return;

                var cle = livingEntity.getBukkitLivingEntity();
                cle.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0));
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.startGnomificate(cle), 20);
            }

            @Override
            public void stop() {
                super.stop();
                this.entity.setIsCastingSpell(IllagerSpell.NONE);
            }

            public void startGnomificate(LivingEntity livingEntity) {
                var target = this.entity.getTarget();
                if (target == null) return;
                this.gnomificate(target.getBukkitLivingEntity(), true);
                this.tick(livingEntity.getUniqueId());
            }

            public void gnomificate(@NotNull LivingEntity livingEntity, boolean gnomificate) {
                var scale = livingEntity.getAttribute(Attribute.SCALE);
                if (scale == null) return;
                if (gnomificate) {
                    setTimeLeft(livingEntity, 10 + getTimeLeft(livingEntity));
                    scale.setBaseValue(scale.getValue() / 2);
                    livingEntity.playSound(Sound.sound(Key.key("block.vault.open_shutter"), Sound.Source.HOSTILE, 0.75F, 1.25F));
                    livingEntity.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.HOSTILE, 0.65F, 1.55F));
                } else {
                    scale.setBaseValue(1);
                    livingEntity.playSound(Sound.sound(Key.key("block.vault.open_shutter"), Sound.Source.HOSTILE, 0.75F, 1.75F));
                }
            }

            public void tick(UUID uuid) {
                if (!this.ticks.containsKey(uuid)) {
                    var tickId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
                        var entity = Bukkit.getEntity(uuid);

                        if (!(entity instanceof LivingEntity livingEntity)) return;

                        if (entity instanceof org.bukkit.entity.Player player && !player.isOnline()) return;

                        livingEntity.sendActionBar(Component.text("" + getTimeLeft(livingEntity)));

                        if (getTimeLeft(livingEntity) == 0) {
                            this.gnomificate(livingEntity, false);
                            Bukkit.getScheduler().cancelTask(this.ticks.get(uuid));
                            this.ticks.remove(uuid);
                        } else {
                            setTimeLeft(livingEntity, getTimeLeft(livingEntity) - 1);
                        }
                    }, 0, 20);

                    this.ticks.put(uuid, tickId);
                }
            }

            @Override
            protected void performSpellCasting() {}

            @Override
            protected int getCastingTime() {
                return 40;
            }

            @Override
            protected int getCastingInterval() {
                return 150;
            }

            @Override
            protected SoundEvent getSpellPrepareSound() {
                return SoundEvents.EVOKER_PREPARE_SUMMON;
            }

            @Override
            protected @NotNull IllagerSpell getSpell() {
                return IllagerSpell.SUMMON_VEX;
            }

            public static void setTimeLeft(LivingEntity target, int time) {
                PersistentData.set(target, "gnomification", PersistentDataType.INTEGER, time);
            }

            public static int getTimeLeft(LivingEntity target) {
                var timeLeft = PersistentData.get(target, "gnomification", PersistentDataType.INTEGER);
                return timeLeft == null ? 0 : timeLeft;
            }
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Trickster(super.getLevel());
    }
}
