package org.cataclysm.game.pantheon;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.level.LevelHandler;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PantheonOfCataclysm {
    public final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
    public final PantheonHandler handler = new PantheonHandler(this);

    public World world;
    public Phase phase;

    public void openPantheon() {
        this.phase = Phase.WAITING_FOR_PLAYERS;
        handler.registerAll();
        handler.setUp();
    }

    public void closePantheon() {
        this.phase = Phase.IDDLE;
    }

    public void stopPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonUtils.teleport(player, Dimensions.OVERWORLD.getWorld().getSpawnLocation());
        }
        this.service.shutdownNow();
        Cataclysm.setPantheon(null);
    }

    public enum Phase {
        TWISTED_WARDEN_FIGHT,
        CALAMITY_HYDRA_FIGHT,
        PALE_KING_FIGHT,
        VOID_LORD_FIGHT,
        THE_RAGNAROK_FIGHT,
        FINAL_PHASE,

        BREAK, //Used for a 5-minute break
        WAITING_FOR_PLAYERS, //Used when waiting for players to start the pantheon
        IDDLE, //Used when the pantheon is not active
    }

    public static @NotNull PantheonOfCataclysm createInstance() {
        PantheonOfCataclysm existingPantheon = Cataclysm.getPantheon();
        if (existingPantheon != null) existingPantheon.stopPantheon();

        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        Cataclysm.setPantheon(pantheon);

        return pantheon;
    }
}
