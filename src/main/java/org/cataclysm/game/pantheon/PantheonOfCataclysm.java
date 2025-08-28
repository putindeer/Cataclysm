package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.level.LevelHandler;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.game.world.Dimensions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service;
    private final World world;
    private Phase phase;

    private PantheonOfCataclysm() {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.world = LevelHandler.getOrCreateWorld();
        this.phase = Phase.IDDLE;
    }

    public void openPantheon() {
        this.phase = Phase.WAITING_FOR_PLAYERS;
    }

    public void closePantheon() {
        this.phase = Phase.IDDLE;
    }

    public void startPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonUtils.teleport(player, PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation());
        }
    }

    public void stopPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonUtils.teleport(player, Dimensions.OVERWORLD.getWorld().getSpawnLocation());
        }
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

    public static void buildPantheon() {
        PantheonOfCataclysm existingPantheon = Cataclysm.getPantheon();
        if (existingPantheon != null) existingPantheon.stopPantheon();

        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        PantheonHandler.registerAll();
        PantheonHandler.setUp(true);

        Cataclysm.setPantheon(pantheon);
    }
}
