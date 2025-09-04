package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.api.boss.BossUtils;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities.*;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class PantheonWarden extends PantheonBoss {
    public PantheonWarden() {
        super("Twisted Warden", 10000);
        super.arena = new CataclysmArea(PantheonLevels.TWISTED_CITY.getLocation(), 70);
    }

    public void damage(LivingEntity livingEntity, double damage) {
        super.damage(livingEntity, damage);
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 0));
    }

    public void shriek(double range, double damage, int radius) {
        var start = this.controller.getEyeLocation();
        var direction = start.getDirection().normalize();

        var world = this.controller.getWorld();
        var step = 0.5;

        for (double i = 0; i <= range; i += step) {
            var offset = direction.clone().multiply(i);
            var location = start.clone().add(offset);

            world.spawnParticle(Particle.SONIC_BOOM, location, radius, radius, radius, radius);
            this.getNearbyLivingEntities(location, radius).forEach(fighter -> {
                damage(fighter, damage);
                fighter.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 3));
                fighter.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0));
            });
        }
    }

    @Override
    public void onStart() {
        setBoosted(false);
        setUpBossBar(true);
        setUpAttributes(true);
        getSoundtrack().loop("THEME", 224);
        BossUtils.updateModel(getController(), EntityType.WARDEN, getName());
    }

    @Override
    public void onStop() {
        setUpAttributes(false);
    }

    public void setUpAttributes(boolean setUp) {
        if (setUp) {
            this.setAttribute(Attribute.SCALE, 2.5F);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 2);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.175);
            this.setAttribute(Attribute.STEP_HEIGHT, 3);
        }
        else {
            this.setAttribute(Attribute.SCALE, 1);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 1);
            this.setAttribute(Attribute.MOVEMENT_SPEED, 0.1);
            this.setAttribute(Attribute.STEP_HEIGHT, 0.6);
        }
    }

    @Override
    public void registerSoundtrack() {
        super.soundtrack.addTrack("THEME", Key.key("cataclysm.boss.twisted_warden.theme"));
    }

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new NightmarePantheonAbility(this));
        super.abilityManager.addAbility(new BonfirePantheonAbility(this));
        super.abilityManager.addAbility(new AppleSeedPantheonAbility(this));
        super.abilityManager.addAbility(new ArmageddonPantheonAbility(this));
        super.abilityManager.addAbility(new DevourerPantheonAbility(this));
        super.abilityManager.addAbility(new BulldozerPantheonAbility(this));
        super.abilityManager.addAbility(new TwistedShriekPantheonAbility(this));
    }

    @Override
    public void tick() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            controller.getLocation().getNearbyLivingEntities(3, 3, 3).forEach(livingEntity -> {
                if (livingEntity == controller) return;
                damage(livingEntity, 40);
                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1F, 0.8F);
            });
            if (isBoosted()) {
                controller.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, true, false));
            }
        });
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("<#B9B7BD>☠ <#CAD6B0><obf>||</obf> <#432d61>" + TinyCaps.tinyCaps(this.name) + " <#CAD6B0><obf>||</obf> <#B9B7BD>☠"),
                1.0F,
                BossBar.Color.PURPLE,
                BossBar.Overlay.NOTCHED_6);
    }

    public void playAlarmSound(@NotNull Location location, float volume) {
        var scheduler = Bukkit.getScheduler();

        var world = location.getWorld();
        scheduler.runTask(Cataclysm.getInstance(), () -> world.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, 1.45F));

        super.thread.getService().schedule(() -> {
            scheduler.runTask(Cataclysm.getInstance(), () -> world.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, 0.55F));
        }, 250, TimeUnit.MILLISECONDS);
    }
}