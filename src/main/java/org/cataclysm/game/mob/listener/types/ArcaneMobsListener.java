package org.cataclysm.game.mob.listener.types;

import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.MobUtils;
import org.cataclysm.game.mob.custom.vanilla.enhanced.TwilightVex;
import org.cataclysm.global.utils.items.ItemUtils;

@Registrable
public class ArcaneMobsListener implements Listener {

    @EventHandler
    public void onBreezeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof BreezeWindCharge windCharge)) return;
        if (!(windCharge.getShooter() instanceof LivingEntity shooter)) return;
        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("ARCANE")) return;

        if (!(event.getEntity() instanceof Player)) return;
        event.setDamage(event.getDamage() * 28);
    }

    @EventHandler
    public void onEvokerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof EvokerFangs fangs)) return;
        if (fangs.getOwner() == null) return;

        String mobId = CataclysmMob.getID(fangs.getOwner());
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("ARCANE")) return;

        if (!(event.getEntity() instanceof Player player)) return;
        event.setCancelled(true);
        player.getLocation().createExplosion(fangs.getOwner(), 2, false, false);
        player.getWorld().strikeLightningEffect(player.getLocation());
    }

    @EventHandler
    public void onVexSummon(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SPELL || !(event.getEntity() instanceof Vex vex)) return;
        if (vex.getSummoner() == null) return;
        String mobId = CataclysmMob.getID(vex.getSummoner());
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("ARCANE")) return;

        var location = vex.getLocation();
        var level = ((CraftWorld) location.getWorld()).getHandle();
        new TwilightVex(level).addFreshEntity(location);
        vex.remove();
    }

    @EventHandler
    private void onSculptureDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        String mobId = CataclysmMob.getID(livingEntity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("SCULPTURE")) return;

        var damager = event.getDamager();
        if (damager instanceof Player player) {
            var mainHand = player.getInventory().getItemInMainHand();
            if (ItemUtils.isPickaxe(mainHand.getType())) {
                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ITEM_MACE_SMASH_GROUND, SoundCategory.BLOCKS, 1.0F, 2F);
                return;
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onChangeTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        String mobId = CataclysmMob.getID(livingEntity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("ARCANE")) return;

        if (!(event.getTarget() instanceof Player)) return;
        if (!MobUtils.hasNearbyPlayer(livingEntity, 20, 3, 20)) return;

        PersistentData.set(livingEntity, "hasTracked", PersistentDataType.BOOLEAN, true);
    }

}
