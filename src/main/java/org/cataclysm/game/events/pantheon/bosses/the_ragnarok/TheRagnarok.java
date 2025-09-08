package org.cataclysm.game.events.pantheon.bosses.the_ragnarok;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;

@Getter
public class TheRagnarok extends PantheonBoss {
    private final TheRagnarokEvents eventManager;
    private TheRagnarokPhases phase;

    public TheRagnarok() {
        super("The Ragnarök", 10000);
        super.arena = new CataclysmArea(PantheonLevels.STORMS_EYE.getLocation(), 60);
        this.eventManager = new TheRagnarokEvents(this);
    }

    @Override
    public void onStart() {
        this.eventManager.handleSetup();
        this.changePhase(TheRagnarokPhases.BATTLE);
    }

    public void changePhase(TheRagnarokPhases phase) {
        this.phase = phase;
        this.eventManager.handleEvents(phase);
    }

    @Override
    public void registerSoundtrack() {
        soundtrack.addTrack("THEME", Key.key("cataclysm.ragnarok.theme"));
    }

    @Override
    public void registerAbilities() {
        // TODO: Añadir habilidades (rugido, meteoros, invocación, etc.)
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                getBarName(false),
                0.0F,
                BossBar.Color.PURPLE,
                BossBar.Overlay.NOTCHED_20
        );
    }

    public Component getBarName(boolean obf) {
        String name = "";
        if (obf) name = "<obf>" + name + "</obf>";
        return MiniMessage.miniMessage().deserialize(
                "<#F0F0F0>⚔ <#7A1C1C><obf>||<reset> " +
                        "<gradient:#7A1C1C:#B22222:#7A1C1C>" + name + "</gradient> " +
                        "<#7A1C1C><obf>||<reset> <#F0F0F0>⚔"
        );
    }
}