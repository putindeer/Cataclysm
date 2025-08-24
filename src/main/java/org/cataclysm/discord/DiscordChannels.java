package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.entities.TextChannel;
import org.cataclysm.Cataclysm;

@Getter
public enum DiscordChannels {
    COMBAT_LOG("1368957736640839742"),
    DEATH_LOG("1367592200833204436"),
    TOTEM_LOG("1367592242264408154"),
    CHAT_LOG("1368957651337085039"),

    ;

    private final TextChannel textChannel;

    DiscordChannels(String channelId) {
        this.textChannel = Cataclysm.getDiscord().getJda().getTextChannelById(channelId);
    }
}
