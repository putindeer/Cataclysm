package org.cataclysm.game.pantheon;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.pantheon.level.entrance.EntranceManager;
import org.cataclysm.game.pantheon.service.PantheonService;
import org.cataclysm.game.pantheon.utils.PantheonBuilder;
import org.cataclysm.game.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.game.pantheon.enums.PantheonPhases;

@Getter
public class PantheonOfCataclysm {
    private final PantheonDispatcher dispatcher;
    private final PantheonManager handler;
    private final PantheonService service;

    public PantheonOfCataclysm() {
        this.dispatcher = new PantheonDispatcher(this);
        this.handler = new PantheonManager(this);
        this.service = new PantheonService();
    }

    public void openPantheon() {
        EntranceManager.setUp(true);
        this.handler.changePhase(PantheonPhases.WAITING);
    }

    public void closePantheon() {
        EntranceManager.setUp(false);
        this.handler.changePhase(PantheonPhases.IDDLE);
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
