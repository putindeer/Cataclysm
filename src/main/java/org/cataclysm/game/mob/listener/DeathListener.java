package org.cataclysm.game.mob.listener;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.custom.vanilla.enhanced.TwilightVex;

import java.util.Random;
import java.util.SplittableRandom;

@Registrable
public class DeathListener implements Listener {

    @EventHandler
    public void deathListener(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        Location location = entity.getLocation();
        int day = Cataclysm.getDay();

        switch (entity.getType()) {
            case SHULKER -> {
                event.getDrops().clear();
                if (new Random().nextInt(100) >= 66 || Cataclysm.getEventManager() != null) {
                    Item shulkerShell = location.getWorld().dropItemNaturally(location, new ItemStack(Material.SHULKER_SHELL));
                    shulkerShell.setInvulnerable(true);
                }
            }

            case EVOKER -> {
                if (day >= 7) {
                    event.getDrops().clear();

                    BlockFace[] faces = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
                    for (var face : faces) {
                        var spawnLocation = location.getBlock().getRelative(face).getLocation();
                        new TwilightVex(((CraftWorld) spawnLocation.getWorld()).getHandle()).addFreshEntity(spawnLocation);
                    }
                }
            }

            case SPIDER, SLIME, MAGMA_CUBE -> {
                if (day >= 7) event.getDrops().clear();
            }

            case BAT -> {
                if (day >= 14) {
                    location.getWorld().spawnEntity(location, EntityType.VEX, CreatureSpawnEvent.SpawnReason.DEFAULT);
                }
            }

            case LLAMA -> {
                if (day >= 14) {
                    event.getDrops().clear();
                    location.getWorld().dropItemNaturally(location, CataclysmItems.LLAMA_FUR.build());
                }
            }

            case ELDER_GUARDIAN -> {
                if (day >= 14) {
                    event.getDrops().clear();
                    location.getWorld().dropItemNaturally(location, CataclysmItems.GUARDIAN_HEART.build());
                }
            }

            case DROWNED -> {
                if (day >= 14) {
                    event.getDrops().clear();
                    int rarity = day >= 28 ? 5 : 100;
                    if (new SplittableRandom().nextInt(0, 100) < rarity) location.getWorld().dropItemNaturally(location, CataclysmItems.DROWNED_CROWN.build());
                }
            }

            case CHICKEN -> {
                if (day >= 21) {
                    if (!entity.getPassengers().isEmpty()) location.createExplosion(entity, 5, false, false);
                }
            }
        }
    }
}
