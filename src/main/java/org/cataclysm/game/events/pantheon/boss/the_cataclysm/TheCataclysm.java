package org.cataclysm.game.events.pantheon.boss.the_cataclysm;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonZones;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.the_ragnarok.TheRagnarok;
import org.cataclysm.game.events.pantheon.boss.the_cataclysm.utils.TheCataclysmEvents;
import org.cataclysm.game.events.pantheon.boss.the_cataclysm.utils.TheCataclysmPhases;

@Getter
public class TheCataclysm extends PantheonBoss {
    private @Setter TheRagnarok ragnarok;

    private final TheCataclysmEvents eventManager;

    private TheCataclysmPhases phase;

    public TheCataclysm() {
        super("The Cataclysm", 3000);
        super.arena = new CataclysmArea(PantheonZones.STORMS_EYE.getLocation(), 100);
        this.eventManager = new TheCataclysmEvents(this);
    }

    @Override
    public void onStart() {
        this.eventManager.handleSetup();
        this.changePhase(TheCataclysmPhases.INTRO);
    }

    public void changePhase(TheCataclysmPhases phase) {
        this.phase = phase;
        this.eventManager.handleEvents(phase);
    }

    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(getController())) return;
        livingEntity.damage(amount);
        livingEntity.setNoDamageTicks(25);
    }

    @Override
    public void registerSoundtrack() {
        soundtrack.addTrack("THUNDERCLAP", Key.key("cataclysm.cataclysm.thunderclap"));
        soundtrack.addTrack("PANDEMONIUM", Key.key("cataclysm.cataclysm.pandemonium"));
        soundtrack.addTrack("RAGNAROK", Key.key("cataclysm.ragnarok.theme"));
        soundtrack.addTrack("DEFEATABLE", Key.key("cataclysm.cataclysm.defeatable"));
    }

    @Override
    public void registerAbilities() {
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                getBarName(false),
                0.0F,
                BossBar.Color.RED,
                BossBar.Overlay.NOTCHED_20);
    }

    public Component getBarName(boolean obf) {
        String name = "ᴛʜᴇ ᴄᴀᴛᴀᴄʟʏꜱᴍ";
        if (obf) name = "<obf>" + name + "</obf>";
        return MiniMessage.miniMessage().deserialize("<#F0F0F0>☠ <#91583A><obf>||<reset> <gradient:#8C3434:#752B2B:#8C3434>" + name + "</gradient> <#91583A><obf>||<reset> <#F0F0F0>☠");
    }
}
