package org.cataclysm.game.world.ragnarok;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.cataclysm.QuantumReactor;
import org.cataclysm.game.mob.custom.cataclysm.twisted.TwistedCreeper;
import org.cataclysm.game.mob.custom.dungeon.temple.Headsman;
import org.cataclysm.game.mob.custom.dungeon.temple.Sentinel;
import org.cataclysm.game.mob.listener.spawn.SpawnUtils;
import org.cataclysm.game.player.PlayerUtils;
import org.cataclysm.game.world.Dimensions;

import java.util.concurrent.ThreadLocalRandom;

@Registrable
public class RagnarokListener implements Listener {

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getLocation().getWorld().equals(Dimensions.THE_END.createWorld())) return;
        var ragnarok = Cataclysm.getRagnarok();
        if (ragnarok == null) return;

        var ragnarokLevel = ragnarok.getData().getLevel();
        if (ragnarokLevel == 1) return;

        var random = ThreadLocalRandom.current().nextInt(0, 100);

        var entity = event.getEntity();
        var level = ((CraftWorld) entity.getWorld()).getHandle();
        var location = entity.getLocation();
        int percentage = 10;
        if (ragnarokLevel >= 2) percentage = 30;

        var type = event.getEntityType();
        switch (type) {
            case ZOMBIE -> {
                if (percentage < random) return;
                var headsman = new Headsman((level));
                headsman.addFreshEntity(location);
                SpawnUtils.setMobCap(headsman.getBukkitLivingEntity(), 8, 2, 1);
                entity.remove();
            }

            case SKELETON -> {
                if (ragnarokLevel < 3 || percentage < random) return;
                var sentinel = new Sentinel((level));
                sentinel.addFreshEntity(location);
                SpawnUtils.setMobCap(sentinel.getBukkitLivingEntity(), 8, 2, 1);
                entity.remove();
            }
        }
    }

    @EventHandler
    private void onLightningStrike(LightningStrikeEvent event) {
        if (Cataclysm.getBoss() != null) return;
        var ragnarok = Cataclysm.getRagnarok();
        if (ragnarok == null) return;
        var ragnarokLevel = ragnarok.getData().getLevel();

        if (ragnarokLevel >= 7) {
            var random = ThreadLocalRandom.current();
            var level = ((CraftWorld) event.getWorld()).getHandle();
            CataclysmMob creeper = new TwistedCreeper(level);
            if (random.nextBoolean()) creeper = new QuantumReactor(level);
            CataclysmMob finalCreeper = creeper;

            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> finalCreeper.addFreshEntity(event.getLightning().getLocation()), 60L);
        }
    }

    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        var ragnarok = Cataclysm.getRagnarok();
        if (ragnarok == null) return;
        ragnarok.getBossBar().addViewer(event.getPlayer());
    }

    @EventHandler
    private void onPlayerBedEnterEvent(PlayerBedEnterEvent event) {
        if (Cataclysm.getRagnarok() == null || Cataclysm.getDay() >= 7) return;
        PlayerUtils.cancelSleep(event);
    }

}
