package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.cataclysm.Cataclysm;

public enum DiscordChannels {
    DEATH_LOG("discord_death_channel_id"),
    TOTEM_LOG("discord_totem_channel_id"),
    CHAT_LOG("discord_chat_channel_id");

    private final String path;
    @Getter private TextChannel textChannel;

    DiscordChannels(String path) {
        this.path = path;
    }

    public String getID() {
        return Cataclysm.getInstance().getConfig().getString(path);
    }

    public static void reload() {
        for (DiscordChannels channel : values()) {
            String id = channel.getID();
            if (id == null || id.isBlank()) {
                channel.textChannel = null;
                continue;
            }
            channel.textChannel = Cataclysm.getDiscord()
                    .getJda()
                    .getTextChannelById(id);
        }
    }
}