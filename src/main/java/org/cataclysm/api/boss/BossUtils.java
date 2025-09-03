package org.cataclysm.api.boss;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.DisguiseConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BossUtils {

    public static void removeModel(Player player) {
        if (DisguiseAPI.getDisguise(player) != null) DisguiseAPI.getDisguise(player).removeDisguise();
    }

    public static void updateModel(Player player, EntityType type, String name) {
        if (DisguiseAPI.getDisguise(player) != null) DisguiseAPI.getDisguise(player).removeDisguise();
        DisguiseConfig.setPlayerNameType(DisguiseConfig.PlayerNameType.VANILLA);

        Entity entity = player.getWorld().spawnEntity(player.getLocation(), type, CreatureSpawnEvent.SpawnReason.CUSTOM);
        entity.customName(MiniMessage.miniMessage().deserialize(name));
        entity.setCustomNameVisible(false);

        DisguiseAPI.disguiseToAll(player, DisguiseAPI.constructDisguise(entity));
        DisguiseAPI.setActionBarShown(player, false);
        DisguiseAPI.getDisguise(player).getInternals().setSelfDisguiseTallScaleMax(0.01);

        entity.remove();
    }

}
