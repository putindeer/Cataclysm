package org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLordThemes;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.time.Duration;

public class VoidOrchestraPhases {
    public enum Phases {VOID_LORD, PALE_KING}

    private final VoidLord lord;

    public VoidOrchestraPhases(VoidLord lord) {
        this.lord = lord;
    }

    protected void startVoidLordPhase() {
        Bukkit.getConsoleSender().sendMessage("start Void Lord Phase");
        this.lord.setName("Void Lord");
        this.lord.setModelPrefix("vl");
        this.lord.updateModel(EntityType.CREEPER, this.lord.getModelPrefix() + "-normal");
        this.lord.handleBossBar(3);

        this.loopTheme(Phases.VOID_LORD);
        this.showTitle(Phases.VOID_LORD);
    }

    protected void startPaleKingPhase() {
        this.lord.setName("Pale King");
        this.lord.setModelPrefix("pk");
        this.lord.updateModel(EntityType.CREEPER, this.lord.getModelPrefix() + "-normal");
        this.lord.handleBossBar(1);

        this.loopTheme(Phases.PALE_KING);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            this.showTitle(Phases.PALE_KING);
        }, 75);
    }

    private void loopTheme(Phases phase) {
        VoidLordThemes theme = VoidLordThemes.valueOf(phase.name());
        theme.loopTheme(this.lord.getSoundtrack());
    }

    private void showTitle(Phases phase) {
        Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
        audience.showTitle(this.getTitle(phase));
        audience.playSound(Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.MASTER, 3.0F, .55F));
        audience.playSound(Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.MASTER, 3.0F, .7F));
        audience.playSound(Sound.sound(Key.key("item.trident.thunder"), Sound.Source.MASTER, 2.0F, .75F));
    }

    private Title getTitle(Phases phase) {
        Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(3), Duration.ofSeconds(2));
        return switch (phase) {
            case PALE_KING -> Title.title(
                    MiniMessage.miniMessage().deserialize("<#ffffff><obf>||</obf> <gradient:#d4d4d4:#ffffff:#d4d4d4>Pale King <#ffffff><obf>||</obf>"),
                    MiniMessage.miniMessage().deserialize(TinyCaps.tinyCaps("<#A1A1A1>ʀᴀᴅɪᴀɴᴛ ᴋɪɴɢ ᴏꜰ ʟɪꜰᴇ")),
                    times);
            case VOID_LORD -> Title.title(
                        MiniMessage.miniMessage().deserialize("<#ffffff><obf>||</obf> <gradient:#2e2e2e:#ffffff:#424242>Void Lord <#ffffff><obf>||</obf>"),
                        MiniMessage.miniMessage().deserialize(TinyCaps.tinyCaps("<#A1A1A1>ᴍɪɢʜᴛʏ ɢᴏᴅ ᴏꜰ ɴᴏᴛʜɪɴɢɴᴇꜱꜱ")),
                        times);
        };
    }
}
