package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.game.pantheon.phase.PantheonPhase;
import org.cataclysm.game.pantheon.phase.PhaseChanger;
import org.cataclysm.game.pantheon.utils.PantheonGlobalUtils;
import org.cataclysm.game.world.Dimensions;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service;
    private final World world;
    private final PhaseChanger phaseChanger;

    private PantheonOfCataclysm() {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.world = PantheonHandler.getOrCreateWorld();
        this.phaseChanger = new PhaseChanger(this);
        this.phaseChanger.changePhase(PantheonPhase.IDDLE);
    }

    public void openPantheon() {
        this.phaseChanger.changePhase(PantheonPhase.WAITING);
    }

    public void closePantheon() {
        this.phaseChanger.changePhase(PantheonPhase.IDDLE);
    }

    public void startPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonGlobalUtils.teleport(player, PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation());
        }
    }

    public void stopPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PantheonGlobalUtils.teleport(player, PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation());
        }
        Cataclysm.setPantheon(null);
    }

    public PantheonPhase getPhase() {
        return this.phaseChanger.getPhase();
    }

    public static void buildPantheon() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        Cataclysm.setPantheon(pantheon);

        PantheonHandler.registerAll();
        PantheonHandler.setUp(false);
        PantheonHandler.setUp(true);
    }
}
