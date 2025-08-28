package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.handlers.PantheonGlobalHandler;
import org.cataclysm.game.pantheon.handlers.PantheonPhaseHandler;
import org.cataclysm.game.pantheon.world.PantheonLocations;
import org.cataclysm.game.pantheon.phase.PantheonPhase;
import org.cataclysm.game.pantheon.handlers.PantheonPlayerHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service;
    private final World world;
    private final PantheonPhaseHandler pantheonPhaseHandler;

    private PantheonOfCataclysm() {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.world = PantheonGlobalHandler.getOrCreateWorld();
        this.pantheonPhaseHandler = new PantheonPhaseHandler(this);
    }

    public void openPantheon() {
        this.pantheonPhaseHandler.changePhase(PantheonPhase.WAITING);
    }

    public void closePantheon() {
        this.pantheonPhaseHandler.changePhase(PantheonPhase.IDDLE);
    }

    public void startPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonPlayerHandler.teleport(player, PantheonLocations.PANTHEON_ENTRANCE.getCoreLocation());
        }
    }

    public void stopPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonPlayerHandler.teleport(player, PantheonLocations.PANTHEON_ENTRANCE.getCoreLocation());
        }
        Cataclysm.setPantheon(null);
    }

    public PantheonPhase getPhase() {
        return this.pantheonPhaseHandler.getPhase();
    }

    public static void create() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        pantheon.getPantheonPhaseHandler().changePhase(PantheonPhase.IDDLE);

        Cataclysm.setPantheon(pantheon);

        PantheonGlobalHandler.registerAll();
        PantheonGlobalHandler.setUp(false);
        PantheonGlobalHandler.setUp(true);
    }
}
