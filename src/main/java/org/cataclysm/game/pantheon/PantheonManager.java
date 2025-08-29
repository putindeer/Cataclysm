package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.enums.PantheonPhases;

@Getter @Setter
public class PantheonManager {
    private PantheonPhases phase;

    private final PantheonOfCataclysm pantheon;

    public PantheonManager(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void changePhase(PantheonPhases phase) {
        this.castDefaults(this.phase, phase);
        this.phase = phase;
        switch (phase) {
            case WAITING -> this.castWaiting();
            case WARDEN_FIGHT -> this.castWardenFight();
        }
    }

    public void castWardenFight() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerHandler.teleport(player, PantheonLevels.WARDEN_ARENA.getCoreLocation());
        }
    }

    public void castWaiting() {
        this.pantheon.getService().getRunner().runEntranceTasks();
        this.pantheon.getDispatcher().sendMessage("El Panteón de Cataclysm ha abierto sus puertas.");
    }

    public void castDefaults(PantheonPhases previousPhase, PantheonPhases phase) {
        //TODO HABIA ALGO AQUI
        if (previousPhase == PantheonPhases.WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) PlayerHandler.setReady(player, false);
        }
    }
}
