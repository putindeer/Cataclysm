package org.cataclysm.game.pantheon.bosses.pale_king;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.pantheon.bosses.pale_king.abilities.BlasarosaAbility;
import org.cataclysm.game.pantheon.bosses.pale_king.abilities.EmbraceTheVoidAbility;
import org.cataclysm.game.pantheon.bosses.pale_king.abilities.KingsoulAbility;
import org.cataclysm.game.pantheon.bosses.pale_king.abilities.TerracismaAbility;
import org.cataclysm.game.pantheon.bosses.pale_king.attacks.PaleDashAttack;
import org.cataclysm.game.pantheon.bosses.pale_king.attacks.PaleScourageAttack;
import org.cataclysm.game.pantheon.bosses.pale_king.attacks.PaleTeleportAttack;
import org.cataclysm.game.pantheon.level.levels.PantheonZones;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.concurrent.TimeUnit;

@Getter
public class PaleKing extends CataclysmBoss {
    public double amplifier = 1;

    private final ItemStack sword;
    public PaleKingPhase phase;

    public PaleKing() {
        super("Pale King", 10000);
        super.arena = PantheonZones.PALE_PALACE.getArena();
        super.listener = new PaleKingListener();
        this.phase = new PaleKingPhase(this);
        this.sword = new ItemStack(Material.IRON_SWORD);
    }

    @Override
    public void onStart() {
        super.soundtrack.loop("PALE", 170);
        this.setUpAttributes(true);

        this.phase.start(1);
        super.controller.getInventory().addItem(this.sword);
    }

    @Override
    public void onStop() {
        this.setUpAttributes(false);
    }

    @Override
    public void registerTracks() {
        super.soundtrack.addTrack("PALE", Key.key("cataclysm.boss.pale_king.paleking_theme"));
        super.soundtrack.addTrack("VOID", Key.key("cataclysm.boss.pale_king.voidlord_theme"));
    }

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new EmbraceTheVoidAbility(this));
        super.abilityManager.addAbility(new KingsoulAbility(this));
        super.abilityManager.addAbility(new BlasarosaAbility(this));
        super.abilityManager.addAbility(new TerracismaAbility(this));

        super.abilityManager.addAbility(new PaleDashAttack(this));
        super.abilityManager.addAbility(new PaleScourageAttack(this));
        super.abilityManager.addAbility(new PaleTeleportAttack(this));
    }

    @Override
    public void tick() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            this.phase.tryElapse();
        });
    }

    public String display;

    @Override
    public BossBar buildBossBar() {
        if (display == null) display = "<" + CataclysmColor.VOID.getColor() +"> ❖ <" + CataclysmColor.VOID.getColor2() + "><obf>||<reset> <gradient:" + CataclysmColor.PALE.getColor() + ":#ffffff:" + CataclysmColor.PALE.getColor() + ">" +
                TinyCaps.tinyCaps(this.name) +
                "</gradient> <" + CataclysmColor.PALE.getColor2() + "> <obf>||<reset> <" + CataclysmColor.PALE.getColor() + ">❖";

        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize(display),
                1.0F,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS);
    }

    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(super.controller)) return;
        livingEntity.damage(amount * amplifier);
        livingEntity.setNoDamageTicks(40);

        if (!(livingEntity instanceof Player player) || !player.isBlocking()) return;
        player.setCooldown(Material.SHIELD, (int) (amount * 10));
    }

    public void playSound(Sound sound, float volume, float pitch) {
        Location location = super.controller.getLocation();
        location.getWorld().playSound(location, sound, volume, pitch);
    }

    public void castSlash() {
        Material swordType = this.sword.getType();
        if (super.controller.hasCooldown(swordType) || !super.controller.getInventory().getItemInMainHand().getType().equals(swordType)) return;

        Location start = super.controller.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        Vector offset = direction.clone().multiply(3);
        Location location = start.clone().add(offset);

        double radius = 1.5;

        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SWEEP_ATTACK, radius + 1, radius * 6);

        location.getNearbyLivingEntities(radius, radius, radius).forEach(livingEntity -> {
            if (livingEntity.equals(super.controller)) return;
            livingEntity.damage(15);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 3F, 1.75F);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 50, 1));
        });

        this.playSound(Sound.ITEM_TRIDENT_RETURN, 3F, 1.75F);
        this.playSound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3F, 1.75F);
        this.playSound(Sound.ENTITY_PLAYER_ATTACK_SWEEP, 3F, 1.25F);

        super.controller.setCooldown(swordType, 5);
    }

    public void castPaleExplosion(Location location, double radius) {
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
                finalLocation.getNearbyLivingEntities(radius).forEach(livingEntity -> {
                    if (livingEntity.equals(super.controller)) return;
                    livingEntity.damage(15 * radius);
                });
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

    private void setUpAttributes(boolean cast) {
        if (cast) {
            this.setAttribute(Attribute.SCALE, 1.5);
            this.setAttribute(Attribute.JUMP_STRENGTH, 1);
            this.setAttribute(Attribute.KNOCKBACK_RESISTANCE, 1);
        } else {
            this.resetAttribute(Attribute.SCALE);
            this.resetAttribute(Attribute.JUMP_STRENGTH);
            this.resetAttribute(Attribute.KNOCKBACK_RESISTANCE);
        }
    }
}
