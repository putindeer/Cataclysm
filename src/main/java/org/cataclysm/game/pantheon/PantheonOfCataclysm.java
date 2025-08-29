package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.handlers.PantheonHandler;
import org.cataclysm.game.pantheon.handlers.PhaseHandler;
import org.cataclysm.game.pantheon.level.PantheonLevelBuilder;
import org.cataclysm.game.pantheon.level.entrance.EntranceManager;
import org.cataclysm.game.pantheon.task.PantheonScheduler;
import org.cataclysm.game.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service =  Executors.newSingleThreadScheduledExecutor();
    private final World world = PantheonLevelBuilder.getOrCreateWorld();

    private final PantheonHandler globalHandler;
    private final PhaseHandler phaseHandler;
    private final PantheonDispatcher dispatcher;
    private final PantheonScheduler taskStorer;

    public PantheonOfCataclysm() {
        this.globalHandler = new PantheonHandler(this);
        this.phaseHandler = new PhaseHandler(this);
        this.dispatcher = new PantheonDispatcher(this);
        this.taskStorer = new PantheonScheduler(this);
    }

    public void openPantheon() {
        EntranceManager.setUp(true);
        this.phaseHandler.changePhase(PhaseHandler.PantheonPhase.WAITING);
    }

    public void closePantheon() {
        EntranceManager.setUp(false);
        this.phaseHandler.changePhase(PhaseHandler.PantheonPhase.IDDLE);
    }

    public void startPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PlayerHandler.teleport(player, PantheonLevels.WARDEN_ARENA.getCoreLocation());
        }
    }

    public void stopPantheon() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 200, 0));
            PlayerHandler.teleport(player, PantheonLevels.PANTHEON_ENTRANCE.getCoreLocation());
        }
        Cataclysm.setPantheon(null);
    }
}
