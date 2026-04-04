package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cataclysm.Cataclysm;

public enum DiscordChannels {
    DEATH_LOG("discord_death_channel_id"),
    TOTEM_LOG("discord_totem_channel_id"),
    CHAT_LOG("discord_chat_channel_id");

    private final String path;
    @Getter private final TextChannel textChannel;

    DiscordChannels(String path) {
        this.path = path;
        this.textChannel = textChannel();
    }

    private TextChannel textChannel() {
        String ID = getID();
        if (ID == null) return null;
        return Cataclysm.getDiscord().getJda().getTextChannelById(ID);
    }

    public String getID() {
        return Cataclysm.getInstance().getConfig().getString(path);
    }
}
