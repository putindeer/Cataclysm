package org.cataclysm.api.structure;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class StructureListener implements Listener {
    private final @NotNull CataclysmStructure structure;

    public StructureListener(@NotNull CataclysmStructure structure) {
        this.structure = structure;
    }

    @EventHandler
    private void onSpawnerSpawn(SpawnerSpawnEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        if (!(StructureUtils.isEntityInStructure(livingEntity, this.structure))) return;

        if (this.structure.mobcap >= 100) {
            livingEntity.remove();
            return;
        }

        var spawnerMobs = this.structure.config.getSpawnerMobs();
        CataclysmMob mob = spawnerMobs.get(livingEntity.getType());

        if (mob == null) return;

        var location = event.getLocation();
        mob.setStructure(this.structure);
        mob.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.SPAWNER);

        livingEntity.remove();
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        var entity = event.getEntity();
        var world = entity.getWorld();
        var location = event.getLocation();
        var entityType = entity.getType();

        if (entity instanceof WindCharge || entity instanceof BreezeWindCharge) return;
        if (!StructureUtils.isLocationInStructure(location, this.structure)) return;
        List<EntityType> types = List.of(
                EntityType.CREEPER,
                EntityType.ARMADILLO,
                EntityType.PUFFERFISH,
                EntityType.SHULKER,
                EntityType.SHULKER_BULLET
        );

        if (entity instanceof Explosive || types.contains(entityType)) {
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 1);
            world.createExplosion(entity, event.getYield(), false, false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onDebug(@NotNull PlayerInteractEvent event) {
        var player = event.getPlayer();
        var itemStack = event.getItem();
        if (event.getAction().isLeftClick()) return;
        if (itemStack == null || !player.isOp()) return;
        if (player.hasCooldown(itemStack.getType())) return;

        if (!StructureUtils.isEntityInStructure(player, this.structure)) return;

        switch (itemStack.getType()) {
            case GOLD_NUGGET -> {
                if (!itemStack.isSimilar(CataclysmItems.REGENERATE_STRUCTURE.build())) return;
                player.setCooldown(itemStack.getType(), 20);
                player.sendMessage(MiniMessage.miniMessage().deserialize("Iniciando restauración de estructura..."));
                player.sendMessage(MiniMessage.miniMessage().deserialize("<#ABABAB>1-. " + this.structure.getUuid()));
                this.structure.delete();
                this.structure.duplicate();
                player.sendMessage(MiniMessage.miniMessage().deserialize("✔ Restauración completada."));

            }

            case GLISTERING_MELON_SLICE -> {
                player.setCooldown(itemStack.getType(), 20);
                ChatMessenger.sendStaffMessage(player, "Dungeon: " + this.structure.config.id);
                player.sendMessage("(" + this.structure.uuid + ")");

                var location = this.structure.level.getLocation();
                var radius = this.structure.config.getRadius();

                if (player.isSneaking()) {
                    var store = this.structure.getMobStore().getStore();
                    player.sendMessage("Mob Store: " + store.size());
                    for (var box : store) player.sendMessage("| - " + box.cataclysmMob().getID());
                } else {
                    player.sendMessage("| - Spawners Looted: " + this.structure.isLooted() + " | " + StructureUtils.getSpawnersInArea(this.structure.level.getLocation(), this.structure.level.radius).size() + "/" + this.structure.config.getSpawnerCount());
                    player.sendMessage("| - MobCap: " + this.structure.mobcap);
                    player.sendMessage("| - Mob Store: " + this.structure.getMobStore().getStore().size());
                    player.sendMessage("| - Location: " + location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
                    player.sendMessage("| - Radius: " + radius);
                    player.sendMessage("| - Variant: " + this.structure.level.variant);
                    player.sendMessage("| - PregeneratedMobs: " + this.structure.config.getPregeneratedMobs().size());
                    for (int i = 0; i < this.structure.config.getPregeneratedMobs().size(); i++) {
                        player.sendMessage("| --> " + this.structure.config.getPregeneratedMobs().get(i).getID());
                    }
                    player.sendMessage("| - SpawnerMobs: " + this.structure.config.getSpawnerMobs().size());
                    for (var set : this.structure.config.getSpawnerMobs().entrySet()) {
                        player.sendMessage("| --> " + set.getValue().getID());
                    }
                }
            }
        }
    }
}