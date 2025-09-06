package org.cataclysm.game.events.pantheon.bosses.void_lord.utils;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class BossBarFormatter {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final String TEMPLATE = "<#%s>❖ <#%s>%s <gradient:#%s:#%s:#%s>%s <#%s>%s <#%s>❖";

    public enum BarStyle {
        PALE_KING("e3e3e3", "a8a8a7", "<obf>||</obf>", "d4d4d4", "ffffff", "d4d4d4", "ᴘᴀʟᴇ ᴋɪɴɢ", "a8a8a7", "<obf>||</obf>", "e3e3e3"),
        HEART_OF_THE_ABYSS("e3e3e3", "a8a8a7", "<obf>||",     "d4d4d4", "ffffff", "d4d4d4", "ᴘᴀʟᴇ ᴋɪɴɢ", "a8a8a7", "||</obf>",   "e3e3e3"),
        VOID_LORD("ffffff", "3d3d3d", "<obf>||</obf>", "2e2e2e", "424242", "2e2e2e", "ᴠᴏɪᴅ ʟᴏʀᴅ", "3d3d3d", "<obf>||</obf>", "ffffff"),
        DREAM_NO_MORE("ffffff", "3d3d3d", "<obf>||",     "2e2e2e", "424242", "2e2e2e", "ᴠᴏɪᴅ ʟᴏʀᴅ", "3d3d3d", "||</obf>",   "ffffff");

        private final String formatted;

        BarStyle(String... args) {
            this.formatted = TEMPLATE.formatted((Object[]) args);
        }

        public Component toComponent() {
            return MINI_MESSAGE.deserialize(this.formatted);
        }
    }

    public static Component formatBarName(int value) {
        return switch (value) {
            case 1 -> BarStyle.PALE_KING.toComponent();
            case 2 -> BarStyle.HEART_OF_THE_ABYSS.toComponent();
            case 3 -> BarStyle.VOID_LORD.toComponent();
            case 4 -> BarStyle.DREAM_NO_MORE.toComponent();
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }

    public static BossBar buildBossBar() {
        return BossBar.bossBar(
                BossBarFormatter.formatBarName(1),
                1.0F,
                BossBar.Color.WHITE,
                BossBar.Overlay.PROGRESS);
    }
}
