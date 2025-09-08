package org.cataclysm.game.events.pantheon.bosses.the_cataclysm;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities.*;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

@Getter @Setter
public class TheCataclysm extends PantheonBoss {
    private boolean chat = true;
    private boolean vulnerable = false;

    private @Setter TheRagnarok ragnarok;

    private final TheCataclysmEvents eventManager;

    private TheCataclysmPhases phase;

    public TheCataclysm() {
        super("The Cataclysm", 3000);
        super.arena = new CataclysmArea(PantheonLevels.STORMS_EYE.getLocation(), 100);
        this.eventManager = new TheCataclysmEvents(this);
    }

    @Override
    public void onStart() {
        this.eventManager.handleSetup();
        this.changePhase(TheCataclysmPhases.INTRO);
        this.updateModel(EntityType.SKELETON, "The Cataclysm");
        this.controller.setGameMode(GameMode.SPECTATOR);
    }

    @Override
    public void onStop() {

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
        soundtrack.addTrack("POWER_OF_FRIENDSHIP", Key.key("cataclysm.cataclysm.power_of_friendship"));
        soundtrack.addTrack("DEFEATABLE", Key.key("cataclysm.cataclysm.defeatable"));
    }

    @Override
    public void registerAbilities() {
        super.getAbilityManager().addAbility(new CataclysmAbility(this));
        super.getAbilityManager().addAbility(new YouSeeBIGGIRLAbility(this));
        super.getAbilityManager().addAbility(new NoGodToSaveYouAbility(this));
        super.getAbilityManager().addAbility(new AnnihilationBeamAbility(this));
        super.getAbilityManager().addAbility(new InfernalRingAbility(this));
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