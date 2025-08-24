package org.cataclysm.api.event;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.global.utils.math.MathUtils;
import org.cataclysm.global.utils.text.TextUtils;

public class EventBossBar {
    private final EventManager event;
    public BossBar bossBar;
    private final CataclysmColor color;

    public EventBossBar(EventManager event, BossBar.Color barColor, CataclysmColor color) {
        this.event = event;
        this.bossBar = BossBar.bossBar(MiniMessage.miniMessage().deserialize(" "), 1, barColor, BossBar.Overlay.PROGRESS);
        this.color = color;
    }

    public void updateProgress() {
        var progress = (float) this.event.timeLeft / this.event.duration;
        this.bossBar.progress(progress);
    }

    public void updateName() {
        var display = TextUtils.formatKey(this.event.id);
        String color1 = "<" + this.color.getColor() + ">";
        String color2 = "<" + this.color.getColor2() + ">";
        String color3 = "<" + this.color.getColor3() + ">";
        this.bossBar.name(MiniMessage.miniMessage().deserialize(color1 + "⌛ " + color2 + display + " " + color1 + "⌛" + color3 + " • " + " " + color1 + MathUtils.formatSeconds(this.event.timeLeft)));
    }
}
