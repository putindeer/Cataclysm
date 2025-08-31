package org.cataclysm.game.events.raids.bosses.calamity_hydra.entity;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftArmorStand;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;

public class SleepingHydra extends ArmorStand {
    public SleepingHydra(Location location) {
        super(EntityType.ARMOR_STAND, ((CraftWorld) location.getWorld()).getHandle());

        CraftArmorStand armorStand = (CraftArmorStand) super.getBukkitEntity();
        armorStand.customName(MiniMessage.miniMessage().deserialize("HydraSleep"));
        armorStand.setCustomNameVisible(false);
        armorStand.setInvulnerable(true);

        AttributeInstance scaleInstance = armorStand.getAttribute(Attribute.SCALE);
        if (scaleInstance != null) scaleInstance.setBaseValue(7.0);

        PersistentData.set(armorStand, "DISGUISE", PersistentDataType.BOOLEAN, true);

        super.getBukkitLivingEntity().teleport(location);
    }
}
