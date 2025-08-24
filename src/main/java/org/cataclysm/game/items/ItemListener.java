package org.cataclysm.game.items;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRiptideEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.mechanics.upgrade.UpgradeCatalogue;
import org.cataclysm.game.player.mechanics.upgrade.Upgrades;
import org.cataclysm.game.player.survival.advancement.CataclysmAdvancement;
import org.cataclysm.game.player.systems.cooldown.PlayerCooldown;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

@Registrable
public class ItemListener implements Listener {

    @EventHandler
    private void onEntityShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var id = new ItemBuilder(event.getBow()).getID();

        if (id == null) return;

        if (id.equalsIgnoreCase("arcane_bow")) {
            player.playSound(Sound.sound(Key.key("block.amethyst_block.break"), Sound.Source.PLAYER, 1F, 1.22F));
            PersistentData.set(event.getProjectile(), "CUSTOM_ARROW", PersistentDataType.STRING, "arcane_arrow");
        }
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        var projectile = event.getEntity();
        if (projectile.getShooter() == null) return;
        if (event.getHitEntity() instanceof Player) return;

        if (projectile instanceof Arrow arrow && event.getHitEntity() instanceof LivingEntity hitEntity) {
            if (hitEntity.getType().equals(EntityType.WARDEN)) return;

            var data = PersistentData.get(arrow, "CUSTOM_ARROW", PersistentDataType.STRING);
            if (data != null && data.equalsIgnoreCase("arcane_arrow")) {
                PotionEffectType[] effects = {PotionEffectType.GLOWING, null};
                if (hitEntity.hasPotionEffect(PotionEffectType.GLOWING)) effects[1] = PotionEffectType.SLOWNESS;
                for (var effect : effects) {
                    if (effect == null) continue;
                    hitEntity.addPotionEffect(new PotionEffect(effect, 100, 0, true, true));
                }
            }
        }
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        var damager = event.getDamager();
        if (damager instanceof Player) return;
        if (!(event.getEntity() instanceof Player player)) return;

        if (!player.isBlocking()) return;

        var inventory = player.getInventory();
        var itemStack = inventory.getItemInMainHand();
        if (itemStack.getType() != Material.SHIELD) itemStack = inventory.getItemInOffHand();

        if (itemStack.getType() != Material.SHIELD) return;

        var id = new ItemBuilder(itemStack).getID();
        if (id != null && id.equalsIgnoreCase("arcane_shield")) {

            if (damager.getType().equals(EntityType.WARDEN)) return;

            if (damager instanceof LivingEntity livingDamager) {
                livingDamager.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
                livingDamager.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.HOSTILE, 0.7F, 2F));
            }

            var direction = damager.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            damager.setVelocity(direction.multiply(0.5));
        }
    }

    @EventHandler
    private void onPlayerRiptide(PlayerRiptideEvent event) {
        var itemStack = event.getItem();
        var id = new ItemBuilder(itemStack).getID();

        if (id != null && id.equalsIgnoreCase("arcane_trident")) {
            var player = event.getPlayer();
            player.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RIPTIDE_2, 0.7F, 0.77F);
            player.playSound(player, org.bukkit.Sound.ITEM_TRIDENT_RIPTIDE_3, 0.7F, 1.17F);
        }
    }

    @EventHandler
    private void onLeftClick(@NotNull PlayerInteractEvent event) {
        if (event.getAction().isRightClick()) return;

        var itemStack = event.getItem();

        if (itemStack == null) return;

        var id = new ItemBuilder(itemStack).getID();

        if (id == null) return;

        var player = event.getPlayer();
        var day = Cataclysm.getDay();
        if (id.equals("lemegeton")) {
            if (Cataclysm.getBossFight() != null && Cataclysm.getBossFight() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;

            if (!player.isSneaking()) return;

            var offHandItemID = new ItemBuilder(player.getInventory().getItemInOffHand()).getID();

            var tier = "cataclysm_upgrade";
            if (day >= 7) tier = "cataclysm_upgrade_tier_2";
            if (day >= 21) tier = "cataclysm_upgrade_tier_3";

            var cataclysmUpgrade = offHandItemID != null && offHandItemID.equals(tier);
            boolean shouldReturn = false;

            if (cataclysmUpgrade) {
                var um = CataclysmPlayer.getCataclysmPlayer(player).getUpgradeManager();
                var currentUpgrades = um.getUpgrades();
                var weekUpgrades = Upgrades.getWeekUpgrades();
                var incursionUpgrade = PersistentData.get(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER);

                var targetUpgrades = incursionUpgrade != null ? weekUpgrades + incursionUpgrade : weekUpgrades;
                shouldReturn = currentUpgrades >= targetUpgrades;
            }

            if (shouldReturn) {
                player.playSound(Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.BLOCK, 1.0F, 1.27F));
            } else {
                new UpgradeCatalogue(player, cataclysmUpgrade).open();
            }
        }
    }

    @EventHandler
    private void onRightClick(@NotNull PlayerInteractEvent event) {
        var action = event.getAction();
        if (action.isLeftClick()) return;
        var itemStack = event.getItem();
        var player = event.getPlayer();

        if (itemStack == null) return;
        if (player.hasCooldown(itemStack)) return;

        var builder = new ItemBuilder(itemStack);
        var id = builder.getID();

        if (id == null) return;

        int cooldown = 0;

        switch (id) {
            case "dungeon_compass": {
                if (builder.hasLodestone()) {
                    player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 2F));
                    return;
                }

                var nearestStructure = CataclysmStructure.getNearestStructure(
                        Dimensions.valueOf(builder.getCustomData("dimension")).getWorld(),
                        builder.getCustomData("structure")
                );

                if (nearestStructure == null) {
                    player.playSound(Sound.sound(Key.key("item.shield.break"), Sound.Source.BLOCK, 1.0F, 1.57F));
                    return;
                }

                builder.setLodestone(nearestStructure.getLevel().getLocation());

                player.playSound(Sound.sound(Key.key("item.lodestone_compass.lock"), Sound.Source.BLOCK, 1.0F, 0.77F));

                break;
            }

            case "lemegeton": {
                if (Cataclysm.getBossFight() != null && Cataclysm.getBossFight() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;

                cooldown = 6000;

                var upgradeManager = CataclysmPlayer.getCataclysmPlayer(player).getUpgradeManager();
                for (var entry : upgradeManager.getActiveUpgrades().entrySet()) {
                    var upgrades = Upgrades.valueOf(entry.getKey().toUpperCase());
                    var definition = upgrades.getDefinition();
                    definition.addEffect(player, 600, upgradeManager.getUpgradeLevel(upgrades));
                }

                player.playSound(Sound.sound(Key.key("item.mace.smash_ground_heavy"), Sound.Source.BLOCK, 1.0F, 0.57F));
                player.playSound(Sound.sound(Key.key("item.trident.thunder"), Sound.Source.BLOCK, 1.0F, 1.27F));
                player.playSound(Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.BLOCK, 1.0F, 0.67F));

                break;
            }

            case "arcane_mace": {
                cooldown = 30;
                var ragnarok = Cataclysm.getRagnarok();
                if (ragnarok != null) {
                    if (ragnarok.getData().getLevel() >= 7) cooldown = 60;
                }

                player.playSound(Sound.sound(Key.key("item.mace.smash_air"), Sound.Source.MASTER, 1.0F, 0.77F));
                player.playSound(Sound.sound(Key.key("block.amethyst_block.break"), Sound.Source.MASTER, 1.0F, 0.77F));

                var charge = player.getWorld().spawn(player.getEyeLocation(), WindCharge.class);
                charge.setShooter(player);

                var direction = player.getLocation().getDirection();
                charge.setVelocity(direction.multiply(2));

                break;
            }

            case "paragons_blessing": {
                if (Cataclysm.getBossFight() != null && Cataclysm.getBossFight() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;

                if (event.getClickedBlock() != null) {
                    var block = event.getClickedBlock();
                    var blockType = block.getType();
                    if (blockType.equals(Material.BARREL)
                            || blockType.equals(Material.SMOKER)
                            || blockType.equals(Material.CRAFTING_TABLE)
                            || blockType.toString().toUpperCase().contains("FURNACE")
                            || blockType.toString().toUpperCase().contains("CHEST")
                            || blockType.toString().toUpperCase().contains("DOOR")
                            || blockType.toString().toUpperCase().contains("GATE")) return;
                }

                cooldown = 20;

                int duration = 300;

                PotionEffect currentEffect = player.getPotionEffect(ImmunityEffect.EFFECT_TYPE);
                if (currentEffect != null) {
                    duration += currentEffect.getDuration();
                }

                player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, duration, 0, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, false));

                player.playSound(Sound.sound(Key.key("block.beacon.activate"), Sound.Source.MASTER, 1.0F, 0.655F));
                player.playSound(Sound.sound(Key.key("entity.iron_golem.death"), Sound.Source.MASTER, 1.0F, 0.855F));
                player.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 1.0F, 1.355F));
                itemStack.setAmount(itemStack.getAmount() - 1);

                var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
                var mortalityManager = cataclysmPlayer.getMortalityManager();

                if (Cataclysm.getDay() >= 21) {
                    float currentValue = mortalityManager.getValue();
                    float newValue = currentValue - 0.0025f;
                    DecimalFormat df = new DecimalFormat("#.####");
                    // Round to 3 decimal places to eliminate floating point errors

                    var ragnarok = Cataclysm.getRagnarok();
                    if (ragnarok != null && ragnarok.getData().getLevel() >= 8) {
                        newValue = currentValue - 0.0075f;
                    }

                    newValue = Float.parseFloat(df.format(newValue));
                    mortalityManager.setValue(newValue);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 2));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, 1));
                }

                var mortalityPercentage = mortalityManager.getPercentage();
                for (var onlinePlayers : Bukkit.getOnlinePlayers()) {
                    ChatMessenger.sendMessage(onlinePlayers, MiniMessage.miniMessage()
                            .deserialize( player.getName() + " activó un <#c6a96e>Paragon's Blessing" + ChatMessenger.getTextColor() + ". ").append(MiniMessage.miniMessage().deserialize(mortalityPercentage)));
                    onlinePlayers.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.MASTER, 0.75F, 1.855F));
                }

                break;
            }
            case "ender_bag": {
                cooldown = 20;
                event.setCancelled(true);
                var enderChest = player.getEnderChest();
                player.openInventory(enderChest);
                player.playSound(Sound.sound(Key.key("block.ender_chest.open"), Sound.Source.BLOCK, 1.0F, 1.27F));

                if (enderChest.contains(Material.PLAYER_HEAD)) new CataclysmAdvancement("the_end/the_boogeyman").grant(player);
                break;
            }
            case "mirages_blessing": {
                if (Cataclysm.getBossFight() != null && Cataclysm.getBossFight() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;

                if (event.getClickedBlock() != null) {
                    var block = event.getClickedBlock();
                    var blockType = block.getType();
                    if (blockType.equals(Material.BARREL)
                            || blockType.equals(Material.SMOKER)
                            || blockType.equals(Material.CRAFTING_TABLE)
                            || blockType.toString().toUpperCase().contains("FURNACE")
                            || blockType.toString().toUpperCase().contains("CHEST")
                            || blockType.toString().toUpperCase().contains("DOOR")
                            || blockType.toString().toUpperCase().contains("GATE")) return;
                }

                cooldown = 20;
                itemStack.setAmount(itemStack.getAmount() - 1);
                int playersQuantity = 0;

                for (var onlinePlayers : Bukkit.getOnlinePlayers()) {
                    ChatMessenger.sendMessage(onlinePlayers, MiniMessage.miniMessage()
                            .deserialize( player.getName() + " activó un <#c6a96e>Mirage Blessing" + ChatMessenger.getTextColor() + "."));
                    onlinePlayers.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.MASTER, 0.75F, 1.855F));

                    if (onlinePlayers == player || onlinePlayers.getWorld() == player.getWorld() && onlinePlayers.getLocation().distance(player.getLocation()) <= 5) {
                        int duration = 400;

                        PotionEffect currentEffect = onlinePlayers.getPotionEffect(ImmunityEffect.EFFECT_TYPE);
                        if (currentEffect != null) {
                            duration += currentEffect.getDuration();
                        }

                        onlinePlayers.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, duration, 0, false));
                        onlinePlayers.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, false));
                        onlinePlayers.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration, 2));
                        for (var effect : player.getActivePotionEffects()) {
                            if (!effect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL)) continue;
                            onlinePlayers.removePotionEffect(effect.getType());
                        }

                        onlinePlayers.setFireTicks(0);
                        onlinePlayers.setFreezeTicks(0);
                        onlinePlayers.setRemainingAir(player.getMaximumAir());
                        onlinePlayers.setExhaustion(0);
                        onlinePlayers.setShieldBlockingDelay(0);
                        onlinePlayers.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 30, 0, false, false));
                        onlinePlayers.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 40, 10));

                        onlinePlayers.playSound(Sound.sound(Key.key("block.beacon.activate"), Sound.Source.MASTER, 1.0F, 0.655F));
                        onlinePlayers.playSound(Sound.sound(Key.key("entity.iron_golem.death"), Sound.Source.MASTER, 1.0F, 0.855F));
                        onlinePlayers.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 1.0F, 1.355F));

                        if (onlinePlayers == player) continue;
                        playersQuantity++;
                        ChatMessenger.sendMessage(onlinePlayers, MiniMessage.miniMessage()
                                .deserialize("Has recibido los efectos del <#c6a96e>Mirage Blessing" + ChatMessenger.getTextColor() + " de " + player.getName() + "."));
                    }
                }

                if (playersQuantity >= 5) new CataclysmAdvancement("the_end/teammates").grant(player);
                break;
            }
        }

        if (cooldown == 0) return;
        new PlayerCooldown(player, cooldown, itemStack.getType());
    }

    @EventHandler
    public void onPlayerLaunchPearl(PlayerLaunchProjectileEvent event) {
        if (event.getProjectile().getType() != EntityType.ENDER_PEARL) return;

        ItemBuilder builder = new ItemBuilder(event.getItemStack());
        String id = builder.getID();
        if (id != null && id.equalsIgnoreCase("paragon_pearl")) {
            if (Cataclysm.getBossFight() != null && Cataclysm.getBossFight() instanceof PaleKing paleKing && paleKing.phase.getCurrent() > 1) return;
            PersistentData.set(event.getProjectile(), "paragon_pearl", PersistentDataType.BOOLEAN, true);
        }
    }

    @EventHandler
    public void onPlayerHitPearl(ProjectileHitEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player player)) return;
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;

        var enderPearl = event.getEntity();
        if (PersistentData.has(enderPearl, "paragon_pearl", PersistentDataType.BOOLEAN)) {
            int duration = 140;

            PotionEffect currentEffect = player.getPotionEffect(ImmunityEffect.EFFECT_TYPE);
            if (currentEffect != null) {
                duration += currentEffect.getDuration();
            }

            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, duration, 0, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, false));

            for (var onlinePlayers : Bukkit.getOnlinePlayers()) {
                ChatMessenger.sendMessage(onlinePlayers, MiniMessage.miniMessage()
                        .deserialize( player.getName() + " utilizó una <#c6a96e>Paragon Pearl" + ChatMessenger.getTextColor() + "."));
                onlinePlayers.playSound(Sound.sound(Key.key("entity.guardian.death"), Sound.Source.MASTER, 0.75F, 1.855F));
            }

            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                player.playSound(Sound.sound(Key.key("block.beacon.activate"), Sound.Source.MASTER, 1.0F, 0.655F));
                player.playSound(Sound.sound(Key.key("entity.iron_golem.death"), Sound.Source.MASTER, 1.0F, 0.855F));
                player.playSound(Sound.sound(Key.key("item.totem.use"), Sound.Source.MASTER, 1.0F, 1.355F));
            }, 1L);
        }
    }

}
