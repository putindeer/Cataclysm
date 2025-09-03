package org.cataclysm.game.player;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.effect.PaleCorrosionEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.game.mob.custom.cataclysm.QuantumReactor;
import org.cataclysm.game.mob.custom.cataclysm.mirage.MirageEye;
import org.cataclysm.game.mob.custom.cataclysm.pale.PaleBlaze;
import org.cataclysm.game.mob.custom.cataclysm.twisted.TwistedBrute;
import org.cataclysm.game.mob.custom.dungeon.temple.Paragon;
import org.cataclysm.game.mob.custom.vanilla.skeleton.wither.NetherNightmare;
import org.cataclysm.game.mob.utils.TeleportUtils;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;
import org.cataclysm.game.world.Dimensions;

import java.util.concurrent.ThreadLocalRandom;

public class PlayerTask {
    public void startTickTask(int ticks) {

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), this::tick, 0, ticks);
    }

    private void tick() {
        var day = Cataclysm.getDay();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode().isInvulnerable()) continue;
            if (!player.isOnline()) continue;
            handlePlayerHealth(day, player);
            handlePlayerEffects(day, player);
            handleTeleports(day, player);
            handleDisper(player);
            handleAchievements(player);
            handleElytra(day, player);
            handleSpawns(day, player);
        }
    }

    private void handlePlayerHealth(int day, Player player) {
        double defaultHealth = 20.0;
        var ragnarok = Cataclysm.getRagnarok();
        //Debuffs from arcane / cataclyst strays
        var arcaneStrayHealthDebuff = PersistentData.get(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE);
        var healthDebuffTimer = PersistentData.get(player, "ARCANE_STRAY_HEALTH_DEBUFF_TIMER", PersistentDataType.INTEGER);

        if (arcaneStrayHealthDebuff != null) {
            //healtDebuff increases in increments of 2.0 (1 heart), player is left at 0.5 hearts when healthDebuff > defaultHealth via vanilla functions
            defaultHealth -= arcaneStrayHealthDebuff;
        }

        if (healthDebuffTimer != null) {
            if (healthDebuffTimer > 0) {
                //Timer gets reset at 10s everytime an arcane / cataclyst stray hits the player, when it ticks down to 0 the player gets its hp containers back
                PersistentData.set(player, "ARCANE_STRAY_HEALTH_DEBUFF_TIMER", PersistentDataType.INTEGER, healthDebuffTimer - 1);
                player.sendActionBar(Component.text("" + healthDebuffTimer));
            } else {
                PersistentData.set(player, "ARCANE_STRAY_HEALTH_DEBUFF", PersistentDataType.DOUBLE, 0.0d);
            }
        }

        var paleCorrosionDebuff = PersistentData.get(player, "PALE_CORROSION_HEALTH_DEBUFF", PersistentDataType.DOUBLE);
        if (player.hasPotionEffect(PaleCorrosionEffect.EFFECT_TYPE)) {
            if (paleCorrosionDebuff != null) {
                Bukkit.getConsoleSender().sendMessage(paleCorrosionDebuff.toString());
                defaultHealth -= paleCorrosionDebuff;
            }
        } else PersistentData.set(player, "PALE_CORROSION_HEALTH_DEBUFF", PersistentDataType.DOUBLE, 0.0);

        // -2 hearts on days 14+
        if (day >= 14) defaultHealth-= 4.0;
        if (day >= 21) defaultHealth-=4.0;
        // Custom Armor
        if (PlayerUtils.hasArmor(ItemFamily.CALAMITY_ARMOR, player)) defaultHealth += 8.0;
        if (PlayerUtils.hasArmor(ItemFamily.PALE_ARMOR, player)) defaultHealth += 16.0;

        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) defaultHealth -= 4.0;

        //Accounting for lemegeton's health blessing
        if (player.hasPotionEffect(PotionEffectType.HEALTH_BOOST)) {
            int level = player.getPotionEffect(PotionEffectType.HEALTH_BOOST).getAmplifier();
            switch (level) {
                case 3 -> defaultHealth += 12.0;
                case 5 -> defaultHealth += 20.0;
                case 7 -> defaultHealth += 28.0;
            }
        }

        //Twisted Relic's + 4 hearts
        var inventory = player.getInventory();
        var itemStack = day < 21 ? CataclysmItems.TWISTED_RELIC.build() : CataclysmItems.MIDWAY_RELIC.build();
        if (inventory.contains(itemStack.getType())) defaultHealth += 8.0;

        //End incursion extra health
        if (Boolean.TRUE.equals(PersistentData.get(player, "END_INCURSION_HEALTH", PersistentDataType.BOOLEAN))) {
            PersistentData.set(player, "END_INCURSION_HEALTH", PersistentDataType.BOOLEAN, false);
            PersistentData.set(player, "INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER, 1);
            defaultHealth += 8.0;
        } else if (Boolean.TRUE.equals(PersistentData.get(player, "NO_END_INCURSION_HEALTH", PersistentDataType.BOOLEAN))) {
            PersistentData.set(player, "NO_END_INCURSION_HEALTH", PersistentDataType.BOOLEAN, false);
            PersistentData.set(player, "NO_INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER, 1);
            defaultHealth += 4.0;
        }

        if (day >= 35) {
            if (inventory.getChestplate() != null && inventory.getChestplate().getType().equals(Material.ELYTRA)) {
                defaultHealth -= 500;
                if (player.hasPotionEffect(PotionEffectType.ABSORPTION)) player.removePotionEffect(PotionEffectType.ABSORPTION);
            }
        }

        //Incursion extra health
        var extraHealth = PersistentData.get(player, "INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER);
        if (extraHealth != null) defaultHealth += extraHealth * 8.0;

        var noExtraHealth = PersistentData.get(player, "NO_INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER);
        if (noExtraHealth != null) defaultHealth += noExtraHealth * 4.0;

        if (PlayerUtils.getMaxHealth(player) != defaultHealth) PlayerUtils.setMaxHealth(player, defaultHealth);
    }

    private void handleDisper(Player player) {
        //Shulker Sculpture's Disper
        if (PlayerUtils.hasMirageHelmet(player)) return;

        var shulkerSculptureNearEntities = player.getNearbyEntities(50.0, 50.0, 50.0);
        for (var entity : shulkerSculptureNearEntities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            var id = CataclysmMob.getID(livingEntity);
            if (id != null && id.equalsIgnoreCase("ShulkerSculpture")) {
                if (player.getPotionEffect(DisperEffect.EFFECT_TYPE) == null) {
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 0.66F, 1.25F);
                }
                player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 30, 0));
                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.55F);
            }
        }

        //Arcane Sculpture's Disper
        var sculptureNearEntities = player.getNearbyEntities(8.0, 8.0, 8.0);
        for (var entity : sculptureNearEntities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            var id = CataclysmMob.getID(livingEntity);
            if (id != null && id.equalsIgnoreCase("ArcaneSculpture")) {
                if (player.getPotionEffect(DisperEffect.EFFECT_TYPE) == null) {
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.BLOCKS, 0.66F, 1.25F);
                }
                player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 30, 0));
                livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 0.5F, 1.55F);
            }
        }

        //Paragon's Disper
        var paragonNearEntities = player.getNearbyEntities(Paragon.DISPER_RADIUS, Paragon.DISPER_RADIUS, Paragon.DISPER_RADIUS);
        for (var entity : paragonNearEntities) {
            if (!(entity instanceof LivingEntity livingEntity)) continue;
            var id = CataclysmMob.getID(livingEntity);
            if (id != null) {
                switch (id) {
                    case "Paragon" -> player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 60, 0));
                    case "PaleParagon" -> {
                        player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 60, 0));
                        player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 60, 0));
                    }
                }
            }
        }

    }

    private void handleAchievements(Player player) {
        //Clownpiece Enjoyer advancement
        var scale = player.getAttribute(Attribute.SCALE);
        if (scale != null && scale.getValue() <= 0.125) {
            new CataclysmAdvancement("the_twisted/average_clownpiece_fan").grant(player);
        }

        //HOLD THE BREATH advancement
        var data = PersistentData.get(player, "MORTEM_TIME", PersistentDataType.INTEGER);
        if (data == null) data = 0;
        if (player.hasPotionEffect(MortemEffect.EFFECT_TYPE)) {
            var inventory = player.getInventory();
            var itemStack = CataclysmItems.MIDWAY_RELIC.build();
            if (!inventory.contains(itemStack.getType())) data = 0;
            else data++;
            PersistentData.set(player, "MORTEM_TIME", PersistentDataType.INTEGER, data);
            if (data >= 15 && player.getGameMode() != GameMode.SPECTATOR) new CataclysmAdvancement("the_end/hold_the_breath").grant(player);
        }
        else if (data != 0) PersistentData.set(player, "MORTEM_TIME", PersistentDataType.INTEGER, 0);
    }

    private void handlePlayerEffects(int day, Player player) {
        var location = player.getLocation();
        var block = location.add(0, -1, 0).getBlock();

        if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            player.setFreezeTicks(0);
        }

        if (PlayerUtils.hasArmor(ItemFamily.PALE_ARMOR, player)) {
            if (player.hasPotionEffect(PaleCorrosionEffect.EFFECT_TYPE)) player.removePotionEffect(PaleCorrosionEffect.EFFECT_TYPE);
            if (player.hasPotionEffect(MortemEffect.EFFECT_TYPE)) player.removePotionEffect(MortemEffect.EFFECT_TYPE);
        }

        if (PlayerUtils.hasMirageHelmet(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 200, 0));
            player.getActivePotionEffects().forEach(potionEffect -> {
                if (potionEffect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL)) player.removePotionEffect(potionEffect.getType());
            });
        }

        if (PlayerUtils.hasTwistedHoe(player)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 60, 1));
        }

        if (day >= 7 && block.getBiome() == Biome.DEEP_DARK && !PlayerUtils.hasMirageHelmet(player) && day < 34) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 2));
        }

        if (day >= 16) {
            if (location.getBlockY() >= 190 && Dimensions.NETHER.createWorld().equals(location.getWorld()) && !PlayerUtils.hasMirageHelmet(player)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 0));
            }

            if (block.getType().equals(Material.MAGMA_BLOCK)) player.setFireTicks(20 * 4);
        }

        if (day >= 21) {
            World world = player.getWorld();
            if (player.isInWater()) {
                Location spawnLocation = world.getSpawnLocation();
                if (!PlayerUtils.hasMirageHelmet(player)) {
                    if (player.getLocation().distance(spawnLocation) > 200) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 60, 0));
                    }
                }

                if (world.equals(Dimensions.PALE_VOID.createWorld())) {
                    if (player.getLocation().distance(spawnLocation) < 200) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 9, true, false, false));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 60, 0, true, false, false));
                    }
                }
            }

            if (!player.getInventory().contains(CataclysmItems.MIDWAY_RELIC.build().getType())) {
                player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 60, 0));
            }
        }

        if (day >= 35) {
            if (player.getWorld().equals(Dimensions.NETHER.createWorld())) {
                player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                player.setFireTicks(20 * 60 * 60);
            }
        }

    }

    private void handleTeleports(int day, Player player) {
        if (day < 14) return;

        for (var enderpearl : player.getEnderPearls()) {
            int livedTicks = 200;
            if (day >= 35) livedTicks = 40;
            if (enderpearl.getTicksLived() >= livedTicks || enderpearl.isInLava() || enderpearl.isInWater() || enderpearl.getFireTicks() > 0) enderpearl.remove();
        }

        if (day < 21) return;
        var standingBlock = player.getLocation().add(0, -1, 0).getBlock();
        if (standingBlock.getType().equals(Material.BEDROCK)) {
            var verticalTeleport = TeleportUtils.findClosestVerticalBlock(player);
            if (verticalTeleport != null) {
                player.teleport(verticalTeleport);
                verticalTeleport.getWorld().playSound(verticalTeleport, Sound.ENTITY_ILLUSIONER_MIRROR_MOVE, 4f, 0.88f);

                var faces = new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
                boolean teleportToMaze = true;
                for (BlockFace face : faces) {
                    if (standingBlock.getRelative(face).getType().equals(Material.BEDROCK)) {
                        teleportToMaze = false;
                        break;
                    }
                }
                if (teleportToMaze) new CataclysmAdvancement("the_end/maze_runner").grant(player);
            }
        }
    }

    private void handleElytra(int day, Player player) {
        if (day >= 28 && player.isInWater()) PlayerUtils.breakElytras(player, 0);
    }

    private void handleSpawns(int day, Player player) {
        if (day < 35) return;
        var mobTimer = PersistentData.get(player, "SPAWN_MOB_TIMER", PersistentDataType.INTEGER);
        if (mobTimer != null) {
            if (mobTimer > 0) {
                PersistentData.set(player, "SPAWN_MOB_TIMER", PersistentDataType.INTEGER, mobTimer - 1);
            } else {
                CataclysmMob mobToSpawn = null;
                var level = ((CraftWorld) player.getWorld()).getHandle();

                switch (ThreadLocalRandom.current().nextInt(5)) {
                    case 0 -> mobToSpawn = new QuantumReactor(level);
                    case 1 -> mobToSpawn = new TwistedBrute(level);
                    case 2 -> mobToSpawn = new NetherNightmare(level);
                    case 3 -> mobToSpawn = new PaleBlaze(level);
                    case 4 -> mobToSpawn = new MirageEye(level);
                }

                mobToSpawn.addFreshEntity(TeleportUtils.getNearestRandomPlayerLocation(player, 50, 2, 6));
                PersistentData.set(player, "SPAWN_MOB_TIMER", PersistentDataType.INTEGER, 5);
            }

        } else PersistentData.set(player, "SPAWN_MOB_TIMER", PersistentDataType.INTEGER, 5);
    }

}
