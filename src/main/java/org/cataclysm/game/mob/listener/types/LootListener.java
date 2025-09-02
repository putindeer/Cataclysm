package org.cataclysm.game.mob.listener.types;

import org.bukkit.Material;
import org.bukkit.block.TrialSpawner;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseLootEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.TrialSpawnerSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.custom.cataclysm.calamity.*;
import org.cataclysm.game.world.Dimensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Registrable
public class LootListener implements Listener {
    private static final Random RANDOM = new Random();

    @EventHandler
    private void onTrialSpawnerSpawn(TrialSpawnerSpawnEvent event) {
        var entity = event.getEntity();
        if (!entity.getWorld().equals(Dimensions.NETHER.createWorld())) return;
        var location = entity.getLocation();
        var level = ((CraftWorld) location.getWorld()).getHandle();
        CataclysmMob mobToSpawn = null;

        switch (entity.getType()) {
            case BLAZE -> mobToSpawn = new CalamityBlaze(level);
            case ENDERMAN -> mobToSpawn = new CalamityEnderman(level);
            case GHAST -> mobToSpawn = new CalamityGhast(level);
            case PIGLIN -> mobToSpawn = new CalamityPiglin(level);
            case WITHER_SKELETON -> mobToSpawn = new CalamitySkeleton(level);
        }

        if (mobToSpawn != null) {
            if ((long) location.getNearbyEntitiesByType(entity.getClass(), 12).size() > 1) {
                event.setCancelled(true);
            } else {
                entity.remove();
                mobToSpawn.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);
                var livingEntity = mobToSpawn.getBukkitLivingEntity();
                mobToSpawn.setSpawnTag(CataclysmMob.SpawnTag.PERSISTENT);
                livingEntity.setPersistent(true);
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, PotionEffect.INFINITE_DURATION, 0));
            }
        }
    }

    @EventHandler
    private void onGenerateLoot(BlockDispenseLootEvent event) {
        var block = event.getBlock();
        var world = block.getWorld();
        if (!world.equals(Dimensions.NETHER.createWorld())) return;
        List<ItemStack> lootPool = List.of();
        int roll = RANDOM.nextInt(100);

        if (block.getType() == Material.TRIAL_SPAWNER) {
            if (roll >= 95) {
                lootPool = buildSpawnerEpicLoot();
            } else if (roll >= 75) {
                lootPool = buildSpawnerRareLoot();
            } else if (roll >= 50) {
                lootPool = buildSpawnerMediumLoot();
            } else {
                lootPool = buildSpawnerCommonLoot();
            }
        }

        if (block.getState() instanceof TrialSpawner spawner) {
            if (spawner.getCooldownLength() < 54000) {
                spawner.setCooldownLength(54000);
                spawner.setCooldownEnd(world.getGameTime() + 54000);
                spawner.update();
            }
        }

        if (lootPool.isEmpty()) return;
        long count = block.getLocation().getNearbyPlayers(12).size();
        List<ItemStack> selectedLoot = getRandomItemsFromList(lootPool, 2, (int) (count + 2));
        event.setDispensedLoot(selectedLoot);
    }

    private List<ItemStack> getRandomItemsFromList(List<ItemStack> source, int min, int max) {
        int count = Math.min(source.size(), RANDOM.nextInt(max - min + 1) + min);

        return source.stream()
                .toList() // Creates mutable copy
                .stream()
                .sorted((a, b) -> RANDOM.nextInt(3) - 1) // Random shuffle
                .limit(count)
                .collect(Collectors.toList());
    }

    private List<ItemStack> buildSpawnerCommonLoot() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(Material.ANCIENT_DEBRIS, 4));
        list.add(new ItemStack(Material.TURTLE_SCUTE, 6));
        if (Cataclysm.getEventManager() != null) list.add(CataclysmItems.CALAMITY_KEY.build());
        return list;
    }

    private List<ItemStack> buildSpawnerMediumLoot() {
        return List.of(
                CataclysmItems.CALAMITY_KEY.build()
        );
    }

    private List<ItemStack> buildSpawnerRareLoot() {
        return Arrays.asList(
                CataclysmItems.CATACLYSM_UPGRADE.build(),
                CataclysmItems.ARCANE_TOTEM.build(),
                cloneWithAmount(CataclysmItems.CALAMITY_CARROT.build(), 12)
        );
    }

    private List<ItemStack> buildSpawnerEpicLoot() {
        return Arrays.asList(
                cloneWithAmount(CataclysmItems.PARAGON_QUARTZ.build(), 4),
                cloneWithAmount(CataclysmItems.CALAMITY_APPLE.build(), 7)
        );
    }

    private ItemStack cloneWithAmount(ItemStack item, int amount) {
        ItemStack clone = item.clone();
        clone.setAmount(amount);
        return clone;
    }
}
