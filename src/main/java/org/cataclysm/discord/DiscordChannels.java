package org.cataclysm.discord;

import net.dv8tion.jda.api.entities.TextChannel;
import org.cataclysm.Cataclysm;

public enum DiscordChannels {
    DEATH_LOG("discord_death_channel_id"),
    TOTEM_LOG("discord_totem_channel_id"),
    CHAT_LOG("discord_chat_channel_id");

    private final String path;

    DiscordChannels(String path) {
        this.path = path;
    }

    public TextChannel getTextChannel() {
        return Cataclysm.getDiscord().getJda().getTextChannelById(getID());
    }

    public String getID() {
        return Cataclysm.getInstance().getConfig().getString(path);
    }
}
