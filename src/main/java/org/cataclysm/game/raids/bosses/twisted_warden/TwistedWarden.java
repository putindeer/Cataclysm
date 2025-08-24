package org.cataclysm.game.raids.bosses.twisted_warden;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.raids.bosses.twisted_warden.abilities.*;
import org.cataclysm.game.raids.structures.RaidStructures;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class TwistedWarden extends CataclysmBoss {
    public TwistedWarden(String name, int health) {
        super(name, health);
        super.arena = RaidStructures.TWISTED_NEST.getStructure().getBossArena();
        super.listener = new TwistedWardenListener();
    }

    @Override
    public void onStart() {
        this.soundtrack.loop("THEME", 224);
    }

    @Override
    public void onStop() {

    }

    @Override
    public void registerTracks() {
        super.soundtrack.addTrack("THEME", Key.key("cataclysm.boss.twisted_warden.theme"));
    }

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new NightmareAbility(this));
        super.abilityManager.addAbility(new BonfireAbility(this));
        super.abilityManager.addAbility(new AppleSeedAbility(this));
        super.abilityManager.addAbility(new ArmageddonAbility(this));
        super.abilityManager.addAbility(new DevourerAbility(this));
        super.abilityManager.addAbility(new BulldozerAbility(this));
        super.abilityManager.addAbility(new TwistedShriekAbility(this));
    }

    @Override
    public void tick() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> damagePlayersSeeing(this.controller));
        if (this.isBoosted()) {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.getController().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 0, true, false)));
        }
    }

    @Override
    public BossBar buildBossBar() {
        var name = "<#B9B7BD>☠ <#CAD6B0><obf>||</obf> <#432d61>" + TinyCaps.tinyCaps(this.name) + " <#CAD6B0><obf>||</obf> <#B9B7BD>☠";
        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(name),
                1.0F,
                BossBar.Color.PURPLE,
                BossBar.Overlay.NOTCHED_6);
    }

    public void shootShriek(double range, double damage, int radius) {
        var start = this.controller.getEyeLocation();
        var direction = start.getDirection().normalize();

        var world = this.controller.getWorld();
        var step = 0.5;

        for (double i = 0; i <= range; i += step) {
            var offset = direction.clone().multiply(i);
            var location = start.clone().add(offset);

            world.spawnParticle(Particle.SONIC_BOOM, location, radius, radius, radius, radius);
            this.getNearbyLivingEntities(location, radius).forEach(fighter -> {
                fighter.damage(damage, this.controller);
                fighter.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0, true, true));
            });
        }
    }

    public void playAlarmSound(@NotNull Location location, float volume) {
        var scheduler = Bukkit.getScheduler();

        var world = location.getWorld();
        scheduler.runTask(Cataclysm.getInstance(), () -> world.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, 1.45F));

        super.thread.getService().schedule(() -> {
            scheduler.runTask(Cataclysm.getInstance(), () -> world.playSound(location, Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, 0.55F));
        }, 250, TimeUnit.MILLISECONDS);
    }

    public static void damagePlayersSeeing(Player controller) {
        controller.getLocation().getNearbyLivingEntities(3).forEach(livingEntity -> {
            if (isPlayerSeeingEntity(controller, livingEntity)) livingEntity.damage(30D, controller);
        });
    }

    public static boolean isPlayerSeeingEntity(Player controller, LivingEntity entity) {
        Location eye = controller.getEyeLocation();
        Vector entityVector = entity.getEyeLocation().toVector().subtract(eye.toVector());
        double dot = entityVector.normalize().dot(eye.getDirection());

        return dot > 0.15D;
    }
}