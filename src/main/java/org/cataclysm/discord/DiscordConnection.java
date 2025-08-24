package org.cataclysm.discord;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

@Getter
public class DiscordConnection {
    private JDA jda;

    public DiscordConnection() {
        try {
            this.jda = JDABuilder.createDefault("MTM5NDczNzczNTc1NDk3NzM4MA.G7fnPE.58WL73LgYtGhYV4D6COQUo1OwiU1map4sJcpJ0")
                    .setActivity(Activity.playing("Cataclysm SMP"))
                    .build();
        } catch (LoginException ignored) {
        }
    }
}
