package org.cataclysm.game.world.ragnarok;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class RagnarokBossBar {
    public final RagnarokData data;

    private final @Getter BossBar blueBar;
    private final @Getter BossBar timeBar;

    public RagnarokBossBar(RagnarokData data) {
        this.data = data;
        this.blueBar = BossBar.bossBar(MiniMessage.miniMessage().deserialize("\uE666"), this.getProgress(), BossBar.Color.BLUE, BossBar.Overlay.PROGRESS);
        this.timeBar = BossBar.bossBar(RagnarokUtils.getFormattedTime(this.data), 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
    }

    public void addViewer(final Player player) {
        this.blueBar.addViewer(player);
        this.timeBar.addViewer(player);
    }

    public void removeViewer(final Player player) {
        this.blueBar.removeViewer(player);
        this.timeBar.removeViewer(player);
    }

    public float getProgress() {
        return (float) this.data.getTimeLeft() / this.data.getDuration();
    }
}
