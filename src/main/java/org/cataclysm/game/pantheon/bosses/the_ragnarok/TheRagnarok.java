package org.cataclysm.game.pantheon.bosses.the_ragnarok;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities.CataclysmAbility;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities.DestroyerAbility;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities.NoGodToSaveYouAbility;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities.YouSeeBIGGIRLAbility;
import org.cataclysm.game.pantheon.level.levels.stormeye.StormEye;

public class TheRagnarok extends CataclysmBoss {
    protected final RagnarokEvents event;
    protected final PantheonOfCataclysm pantheon;

    public TheRagnarok(PantheonOfCataclysm pantheon) {
        super("The Ragnarök", 3500);
        this.pantheon = pantheon;
        super.arena = new CataclysmArea(StormEye.getLocation(), 100);
        this.event = new RagnarokEvents(this);
        super.listener = new RagnarokListener();
    }

    @Override
    public void registerAbilities() {
        super.abilityManager.addAbility(new CataclysmAbility(this));
        super.abilityManager.addAbility(new NoGodToSaveYouAbility(this));
        super.abilityManager.addAbility(new YouSeeBIGGIRLAbility(this));
        super.abilityManager.addAbility(new DestroyerAbility(this));
    }

    @Override
    public void registerTracks() {
        super.soundtrack.addTrack("PHASE_1", Key.key("cataclysm.pantheon.boss.ragnarok.phase_1"));
        super.soundtrack.addTrack("PHASE_2", Key.key("cataclysm.pantheon.boss.ragnarok.phase_2"));
    }

    @Override
    public void onStart() {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.pantheon.getDispatcher().sendActionBar("Deus misereatur animarum suarum...", 2000), 60);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.soundtrack.loop("PHASE_1", 600), 100);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), this.event::entrance, 140);
    }

    @Override
    public void onStop() {
    }

    @Override
    public void tick() {
    }

    public void damage(LivingEntity livingEntity, double amount) {
        if (livingEntity.equals(super.controller)) return;
        livingEntity.damage(amount);
        livingEntity.setNoDamageTicks(40);

        if (!(livingEntity instanceof Player player) || !player.isBlocking()) return;
        player.setCooldown(Material.SHIELD, (int) (amount * 10));
    }

    @Override
    public BossBar buildBossBar() {
        return BossBar.bossBar(
                MiniMessage.miniMessage().deserialize("\uE666"),
                1.0F,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS);
    }
}
