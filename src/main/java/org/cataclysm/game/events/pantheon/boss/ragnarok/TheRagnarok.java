package org.cataclysm.game.events.pantheon.boss.ragnarok;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.boss.ragnarok.abilities.CataclysmAbility;
import org.cataclysm.game.events.pantheon.boss.ragnarok.abilities.DestroyerAbility;
import org.cataclysm.game.events.pantheon.boss.ragnarok.abilities.NoGodToSaveYouAbility;
import org.cataclysm.game.events.pantheon.boss.ragnarok.abilities.YouSeeBIGGIRLAbility;
import org.cataclysm.game.events.pantheon.boss.ragnarok.utils.TheRagnarokEvents;

@Getter @Setter
public class TheRagnarok extends PantheonBoss {
    private final TheRagnarokEvents eventManager;

    public TheRagnarok() {
        super("The Ragnarök", 100000);
        this.eventManager = new TheRagnarokEvents(this);
    }

    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(getController())) return;
        livingEntity.damage(amount);
        livingEntity.setNoDamageTicks(25);
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop() {
    }

    @Override
    public void tick() {
    }

    @Override
    public void registerSoundtrack() {
        soundtrack.addTrack("THEME", Key.key("cataclysm.ragnarok.theme"));
    }

    @Override
    public void registerAbilities() {
        abilityManager.addAbility(new CataclysmAbility(this));
        abilityManager.addAbility(new DestroyerAbility(this));
        abilityManager.addAbility(new YouSeeBIGGIRLAbility(this));
        abilityManager.addAbility(new NoGodToSaveYouAbility(this));
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(Component.text("\uE666"), 1.0F, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
    }
}
