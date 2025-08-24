package org.cataclysm.game.world.ragnarok;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.world.ragnarok.events.RagnarokEndEvent;
import org.cataclysm.game.world.ragnarok.events.RagnarokStartEvent;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public record RagnarokManager(Ragnarok ragnarok) {

    public void start() {
        if (Cataclysm.getRagnarok() == null) {
            for (var players : Bukkit.getOnlinePlayers()) this.ragnarok.getBossBar().addViewer(players);

            this.ragnarok.getThread().run();

            RagnarokUtils.setUpWorld(true);

            Cataclysm.setRagnarok(this.ragnarok);
        } else {
            Cataclysm.getRagnarok().setData(Cataclysm.getRagnarok().getData().append(this.ragnarok.getData()));
        }

        for (var players : Bukkit.getOnlinePlayers()) {
            players.showTitle(this.getTitle());

            players.playSound(Sound.sound(Key.key("cataclysm.ragnarok"), Sound.Source.AMBIENT, 1, 0.95F));

            players.playSound(Sound.sound(Key.key("block.end_portal.spawn"), Sound.Source.AMBIENT, 1.35F, 0.55F));
            players.playSound(Sound.sound(Key.key("entity.skeleton_horse.death"), Sound.Source.AMBIENT, 1.35F, 0.75F));
            players.playSound(Sound.sound(Key.key("entity.elder_guardian.death"), Sound.Source.AMBIENT, 1.15F, 0.5F));

            ChatMessenger.sendMessage(players, "<i><gradient:#6086c8:#4c819c>Deus misereatur animarum suarum...</gradient>");
        }

        new RagnarokStartEvent(this.ragnarok).callEvent();
    }

    public void stop() {
        for (var players : Bukkit.getOnlinePlayers()) {
            ChatMessenger.sendMessage(players, "<i>Beati reliquiae in consummatione sseculi.");

            players.stopSound("cataclysm.ragnarok", SoundCategory.AMBIENT);
            players.playSound(Sound.sound(Key.key("entity.zombie_villager.converted"), Sound.Source.AMBIENT, 1.35F, 0.75F));

            this.ragnarok.getBossBar().removeViewer(players);
        }

        this.ragnarok.getThread().shutdown();

        RagnarokUtils.setUpWorld(false);

        Cataclysm.setRagnarok(null);

        new RagnarokEndEvent().callEvent();

        try {
            var success = new RagnarokLoader().getJsonConfig().getFile().delete();
            if (success) Cataclysm.getInstance().getLogger().info("Ragnarok Data Manager has been deleted.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private @NotNull Title getTitle() {
        assert Cataclysm.getRagnarok() != null;
        Component duration = RagnarokUtils.getFormattedTime(Cataclysm.getRagnarok().getData());

        return Title.title(
                MiniMessage.miniMessage().deserialize("<#478db6><obf><b>||<reset> <#478db6>Ragnarök <#478db6><obf><b>||"),
                MiniMessage.miniMessage().deserialize("<#9b9b9b>Duración: <#478db6>").append(duration),
                Title.Times.times(
                        Duration.ofMillis(1000),
                        Duration.ofMillis(2500),
                        Duration.ofMillis(3000)
                )
        );
    }

}