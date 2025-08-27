package org.cataclysm.game.pantheon;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.cataclysm.Cataclysm;

@CommandAlias("pantheon")
@CommandPermission("admin.perms")
public class PantheonCommand extends BaseCommand {

    @Subcommand("setup")
    private void setUp() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
    }


}
