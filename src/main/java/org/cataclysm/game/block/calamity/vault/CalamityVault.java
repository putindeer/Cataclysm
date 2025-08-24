package org.cataclysm.game.block.calamity.vault;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.block.calamity.vault.drops.CalamityVaultDrops;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CalamityVault {
    private final int size = 4;

    private final LivingEntity caster;
    private final World world;

    public CalamityVault(@NotNull LivingEntity caster) {
        this.caster = caster;
        this.world = caster.getWorld();
    }

    public void open() {
        this.caster.customName(Component.text("Calamity Vault Open"));
        this.setLocked(true);
        this.caster.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, (this.size * 20), 0));
        this.world.playSound(this.caster.getLocation(), Sound.BLOCK_VAULT_OPEN_SHUTTER, 3F, 0.825F);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), this::drop, 40);

        final double radius = 80.0;
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            var lockedVaults = this.getNearbyLockedVaults(radius);
            if (lockedVaults.isEmpty()) this.unlockNearbyVaults(radius);
        }, 40 + (this.size + 1) * 20L);
    }

    public void close() {
        this.setLocked(false);
        this.caster.customName(Component.text("Calamity Vault"));
        this.world.playSound(this.caster.getLocation(), Sound.BLOCK_VAULT_CLOSE_SHUTTER, 3F, 0.825F);
    }

    public void drop() {
        var center = this.caster.getLocation().clone();
        var rewards = CalamityVaultDrops.createLootTable(this.size + center.getNearbyPlayers(20).size());

        final double[] adition = {0.5F};

        for (int i = 0; i < rewards.size(); i++) {
            int finalI = i;
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                var operation = adition[0];

                var location = center.clone();
                if (finalI > 1) location.add(operation, 0, 0);
                else location.add(0, 0.5, operation);

                var world = location.getWorld();
                var item = world.dropItemNaturally(location, rewards.get(finalI));
                item.setPickupDelay(1);
                world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_EJECT_ITEM, SoundCategory.BLOCKS, 2F, 0.825F);

                adition[0] *= -1;
            }, (i * 20L));
        }
    }

    public boolean verify(ItemStack item) {
        var id = new ItemBuilder(item).getID();
        if (id == null) return false;
        return id.equalsIgnoreCase("calamity_key");
    }

    public List<LivingEntity> getNearbyLockedVaults(double radius) {
        List<LivingEntity> nearbyLockedVaults = new ArrayList<>();

        List<LivingEntity> nearbyVaults = this.getNearbyVaults(radius);
        for (var vaultCaster : nearbyVaults) {
            CalamityVault vault = new CalamityVault(vaultCaster);
            if (!vault.isLocked()) nearbyLockedVaults.add(vaultCaster);
        }

        return nearbyLockedVaults;
    }

    public List<LivingEntity> getNearbyVaults(double radius) {
        List<LivingEntity> nearbyVaults = new ArrayList<>();

        Collection<LivingEntity> nearbyLivingEntities = this.caster.getLocation().getNearbyLivingEntities(radius, radius, radius);
        for (var livingEntity : nearbyLivingEntities) {
            if (!isCasteable(livingEntity)) continue;
            nearbyVaults.add(livingEntity);
        }

        return nearbyVaults;
    }

    public void unlockNearbyVaults(double radius) {
        var nearbyVaults = this.getNearbyVaults(radius);
        for (LivingEntity vaultCaster : nearbyVaults) {
            CalamityVault vault = new CalamityVault(vaultCaster);
            vault.close();
        }
    }

    public void setLocked(boolean lock) {
        PersistentData.set(this.caster, "LOCKED", PersistentDataType.BOOLEAN, lock);
    }

    public boolean isLocked() {
        Boolean data = PersistentData.get(this.caster, "LOCKED", PersistentDataType.BOOLEAN);
        return data != null && data;
    }

    public static boolean isCasteable(LivingEntity livingEntity) {
        var id = CataclysmMob.getID(livingEntity);
        return id != null && id.equalsIgnoreCase("CALAMITY_VAULT");
    }
}
