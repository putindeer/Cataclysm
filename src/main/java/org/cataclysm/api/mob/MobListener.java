package org.cataclysm.api.mob;

import org.bukkit.Material;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Registrable
public class MobListener implements Listener {

    @EventHandler
    private void onDebugEntity(PlayerInteractEntityEvent event) {
        var player = event.getPlayer();

        if (!player.getInventory().getItemInMainHand().getType().equals(Material.GLISTERING_MELON_SLICE)) return;

        var entity = event.getRightClicked();
        if (!(entity instanceof LivingEntity livingEntity)) return;

        player.sendMessage("ID: " + CataclysmMob.getID(livingEntity));

        var token = CataclysmMob.getToken(livingEntity);
        if (token == null) return;
        player.sendMessage("Token: " + token.key());

        var structure = CataclysmMob.getStructure(livingEntity);
        if (structure == null) return;
        player.sendMessage("Structure: ");
        player.sendMessage("| - ID: " + structure.getConfig().getId());
        player.sendMessage("| - Uuid: " + structure.getUuid());

        if (player.isSneaking()) livingEntity.remove();
    }

    @EventHandler
    private void onChunkLoad(ChunkLoadEvent event) {
        var chunk = event.getChunk();
        for (var entity : new ArrayList<>(List.of(chunk.getEntities()))) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;

            var id = CataclysmMob.getID(livingEntity);
            if (id == null) continue;

            var spawnTag = CataclysmMob.getSpawnTag(livingEntity);
            if (spawnTag == CataclysmMob.SpawnTag.PERSISTENT) {
                var world = entity.getLocation().getWorld();
                if (world == null) continue;
                var nmsWorld = ((CraftWorld) world).getHandle();

                var mob = CataclysmMob.instantiateMob(id, nmsWorld);
                if (mob == null) continue;

                var structure = CataclysmMob.getStructure(livingEntity);
                if (structure == null) continue;

                structure.getMobStore().getStorer().respawn(livingEntity);
            } else {
                entity.remove();
            }
        }
    }


    @EventHandler
    private void onEntityDeath(@NotNull EntityDeathEvent event) {
        var livingEntity = event.getEntity();

        var id = CataclysmMob.getID(livingEntity);
        if (id == null) return;

        var location = livingEntity.getLocation();

        if (!(location.getWorld() instanceof CraftWorld craftWorld)) return;

        var mob = CataclysmMob.instantiateMob(id, craftWorld.getHandle());
        if (mob == null) return;

        var token = CataclysmMob.getToken(livingEntity);
        if (token == null) return;

        event.getDrops().clear();

        var drops = mob.getDrops();
        if (drops != null) {
            drops.drop(location);
        }

        var dungeon = CataclysmMob.getStructure(livingEntity);
        if (dungeon == null) return;

        var file = MobLoader.getMobFile(dungeon, livingEntity);
        if (file == null) return;
        file.delete();
        var store = dungeon.getMobStore();
        if (store == null || store.getStorer() == null) return;

        store.getStorer().removeFromStore(token);
    }

}