package org.cataclysm.game.events.pantheon.bosses.the_ragnarok;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;

public class TheRagnarokEvents {
    private final TheRagnarok ragnarok;
    private PantheonDispatcher dispatcher;
    private Soundtrack soundtrack;

    public TheRagnarokEvents(TheRagnarok ragnarok) {
        this.ragnarok = ragnarok;
    }

    public void handleEvents(TheRagnarokPhases phase) {
        if (phase == TheRagnarokPhases.BATTLE) this.startBattleSequence();
        dispatcher.resetDelay();
    }

    private void startBattleSequence() {
        soundtrack.loop("THEME", 203);

        // Mensaje inicial a todos los jugadores
        for (Player player : Bukkit.getOnlinePlayers()) {
            dispatcher.sendActionBar("<#FF0000>¡El Fin de los Tiempos ha comenzado!");
            dispatcher.playSounds(
                    Sound.sound(Key.key("entity.wither.spawn"), Sound.Source.MASTER, 1F, 0.8F),
                    Sound.sound(Key.key("entity.ender_dragon.growl"), Sound.Source.MASTER, 0.8F, 0.6F)
            );
        }
    }

    public void handleSetup() {
        if (this.dispatcher == null) this.dispatcher = ragnarok.getPantheon().getDispatcher();
        if (this.soundtrack == null) this.soundtrack = ragnarok.getSoundtrack();
    }
}
