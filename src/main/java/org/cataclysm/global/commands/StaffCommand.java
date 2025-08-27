package org.cataclysm.global.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.ItemCatalogue;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.data.StructureLoader;
import org.cataclysm.game.block.arcane.table.ArcaneTableMob;
import org.cataclysm.api.event.CataclysmEvents;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.effect.PaleCorrosionEffect;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.custom.block.CalamityVault;
import org.cataclysm.game.player.CataclysmPlayer;
import org.cataclysm.game.player.mechanics.upgrade.Upgrades;
import org.cataclysm.game.player.tag.role.RoleManager;
import org.cataclysm.game.player.tag.role.RoleType;
import org.cataclysm.game.player.tag.team.TeamManager;
import org.cataclysm.game.player.tag.team.Teams;
import org.cataclysm.game.raids.bosses.twisted_warden.TwistedWarden;
import org.cataclysm.game.world.Dimensions;
import org.cataclysm.game.world.dungeons.*;
import org.cataclysm.game.world.ragnarok.Ragnarok;
import org.cataclysm.game.world.ragnarok.RagnarokData;
import org.cataclysm.game.world.ragnarok.RagnarokManager;
import org.cataclysm.global.utils.chat.ChatMessenger;
import org.cataclysm.server.tablist.CataclysmTablist;

import java.time.Duration;
import java.util.UUID;

@CommandAlias("staff")
@CommandPermission("admin.perms")
public class StaffCommand extends BaseCommand {
    @Subcommand("palevoid entrance")
    @CommandCompletion("@players true|false")
    private void palevoidEntrance(Player player, boolean entrance) {
        PersistentData.set(player, "HAS-ENTERED-PALE-VOID", PersistentDataType.BOOLEAN, entrance);
        ChatMessenger.sendStaffMessage(player, "Palevoid entrance set to " + entrance);
    }

    @Subcommand("mortem all")
    private void mortem() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, PotionEffect.INFINITE_DURATION, 0));
            player.showTitle(
                    Title.title(
                            MiniMessage.miniMessage().deserialize("<gold><bold>MORTEM</bold></gold>"),
                            MiniMessage.miniMessage().deserialize("<yellow><italic>Que buen momento para un <gold><bold>POSTMORTAL</bold></gold></italic></yellow>"),
                            Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(2))
                    )
            );
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1F, .75F);
        }
    }

    @Subcommand("mortem")
    private void mortem(CommandSender commandSender, String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player == null) return;

        player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, PotionEffect.INFINITE_DURATION, 0));
        player.showTitle(
                Title.title(
                        MiniMessage.miniMessage().deserialize("<gold><bold>MORTEM</bold></gold>"),
                        MiniMessage.miniMessage().deserialize("<yellow><italic>Que buen momento para un <gold><bold>POSTMORTAL</bold></gold></italic></yellow>"),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(2))
                )
        );
        player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1F, .75F);
    }

    @Subcommand("corrosion")
    private void corrosion(CommandSender commandSender, String nickname) {
        Player player = Bukkit.getPlayer(nickname);
        if (player == null) return;

        player.addPotionEffect(new PotionEffect(PaleCorrosionEffect.EFFECT_TYPE, PotionEffect.INFINITE_DURATION, 0));
        player.showTitle(
                Title.title(
                        MiniMessage.miniMessage().deserialize("<gradient:#B0E0E6:white><bold>PALE CORROSION</bold></gradient>"),
                        MiniMessage.miniMessage().deserialize("<gray><italic>Pallum corruptus es!</italic></gray>"),
                        Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(5), Duration.ofSeconds(2))
                )
        );
        player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 1F, .75F);
    }

    @Subcommand("event")
    @CommandCompletion(" start|stop")
    private void event(CataclysmEvents event, String action) {
        var eventManager = event.getEvent();
        switch (action) {
            case "start" -> eventManager.start();
            case "stop" -> eventManager.stop();
        }
    }

    @Subcommand("role set")
    @CommandCompletion(" @players")
    private void roleSet(CommandSender commandSender, RoleType role, String username) {
        var player = Bukkit.getPlayer(username);

        if (player == null) return;

        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
        new RoleManager(cataclysmPlayer.getData()).setRole(role);
        CataclysmTablist.organizePlayer(player);
    }

    @Subcommand("team join")
    @CommandCompletion(" @players")
    private void teamJoin(CommandSender commandSender, Teams teams, String username) {
        var player = Bukkit.getPlayer(username);

        if (player == null) return;

        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
        new TeamManager(cataclysmPlayer.getData()).setTeam(teams);
        CataclysmTablist.organizePlayer(player);
    }


    @Subcommand("teleport")
    private void teleport(CommandSender commandSender, Dimensions dimensions) {
        if (!(commandSender instanceof Player player)) return;
        player.teleport(dimensions.getWorld().getSpawnLocation());
    }

    @Subcommand("structure")
    @Description("Staff command to manage Cataclysm structures.")
    private void structure(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        ChatMessenger.sendStaffMessage(player, "Estructuras disponibles: " + CataclysmStructure.getStructures().size());
    }

    @Subcommand("structure loader load")
    private void structureLoaderLoad(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        ChatMessenger.sendStaffMessage(player, "Cargando estructuras...");
        StructureLoader.loadAll();
        ChatMessenger.sendStaffMessage(player, "Todas las estructuras han sido cargadas correcctamente.");
    }

    @Subcommand("structure list")
    @CommandCompletion("monolith|paragon_temple|calamity_chamber|mirage_citadel|pale_temple")
    private void structureRestore(CommandSender commandSender, String structureName) {
        if (!(commandSender instanceof Player player)) return;

        var structureHashMap = CataclysmStructure.getStructures();

        var amount = 0;
        for (var set : structureHashMap.entrySet()) {
            var structure = set.getValue();
            var config = structure.getConfig();

            if (config.getId().equalsIgnoreCase(structureName)) {
                amount++;
                var location = structure.getLevel().getLocation();
                String teleportMessage = "<click:run_command:'/tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "'>" +
                        "<hover:show_text:'<gray>Click to teleport to structure'>" +
                        amount + "-. <#7A7A7A>" + structure.getUuid() + "</hover></click>";

                player.sendMessage(MiniMessage.miniMessage().deserialize(teleportMessage));
            }
        }
    }

    @Subcommand("structure restore")
    @CommandCompletion("monolith|paragon_temple")
    private void structureRestore(CommandSender commandSender, String structureName, String uuid) {
        if (!(commandSender instanceof Player player)) return;

        player.sendMessage(MiniMessage.miniMessage().deserialize("Iniciando restauración de estructura..."));

        var structure = CataclysmStructure.getStructures().get(UUID.fromString(uuid));
        var config = structure.getConfig();

        if (config.getId().equalsIgnoreCase(structureName) && !structure.isLooted()) {
            var location = structure.getLevel().getLocation();

            player.sendMessage(MiniMessage.miniMessage().deserialize("<#ABABAB>1-. " + structure.getUuid()));

            String coordsClickable = "<click:run_command:'/tp " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ() + "'>" +
                    "<hover:show_text:'<gray>Click to teleport to structure'>" +
                    "<#7A7A7A>" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " (" + config.getId() + ")</hover></click>";

            player.sendMessage(MiniMessage.miniMessage().deserialize(coordsClickable));

            structure.delete();
            structure.duplicate();

            player.sendMessage(MiniMessage.miniMessage().deserialize("✔ Restauración completada."));
        }
    }

    @Subcommand("structure generate PALE_TEMPLE")
    private void structureGeneratePaleTemple(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        for (var staff : Bukkit.getOnlinePlayers()) {
            if (!staff.isOp()) continue;
            ChatMessenger.sendStaffMessage(staff, player.getName() + " esta generando un Pale Temple");
        }

        var structure = new PaleTemple();
        structure.generate(player.getLocation(), "default");
    }

    @Subcommand("structure generate PARAGON_TEMPLE")
    @CommandCompletion("stripes|eye|dark")
    private void structureGenerateTemple(CommandSender commandSender, String variant) {
        if (!(commandSender instanceof Player player)) return;

        for (var staff : Bukkit.getOnlinePlayers()) {
            if (!staff.isOp()) continue;
            ChatMessenger.sendStaffMessage(staff, player.getName() + " esta generando un Paragon Temple (" + variant.toUpperCase() + ")");
        }

        var structure = new ParagonTemple();
        structure.generate(player.getLocation(), variant);
    }

    @Subcommand("structure generate MONOLITH")
    @CommandCompletion("stone|deepslate")
    private void structureGenerateMonolith(CommandSender commandSender, String variant) {
        if (!(commandSender instanceof Player player)) return;

        for (var staff : Bukkit.getOnlinePlayers()) {
            if (!staff.isOp()) continue;
            ChatMessenger.sendStaffMessage(staff, player.getName() + " esta generando un Monolito (" + variant.toUpperCase() + ")");
        }

        var structure = new Monolith();
        structure.generate(player.getLocation(), variant);
    }

    @Subcommand("structure generate CALAMITY_CHAMBERS")
    private void structureGenerateChamber(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        for (var staff : Bukkit.getOnlinePlayers()) {
            if (!staff.isOp()) continue;
            ChatMessenger.sendStaffMessage(staff, player.getName() + " esta generando una Calamity Chamber");
        }

        var structure = new CalamityChamber();
        structure.generate(player.getLocation(), "default");
    }

    @Subcommand("structure generate MIRAGE_CITADEL")
    private void structureGenerateMirageCitadel(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;

        for (var staff : Bukkit.getOnlinePlayers()) {
            if (!staff.isOp()) continue;
            ChatMessenger.sendStaffMessage(staff, player.getName() + " esta generando una Mirage Citadel");
        }

        var structure = new MirageCitadel();
        structure.generate(player.getLocation(), "default");
    }

    @Subcommand("give")
    @Description("Staff command to give a specified Cataclysm Item to a player.")
    private void give(CommandSender commandSender, @Optional CataclysmItems item, @Optional Integer amount) {
        if (!(commandSender instanceof Player player)) return;

        if (item == null) {
            new ItemCatalogue(player).open();
            return;
        }

        if (amount == null) amount = 1;

        var itemStack = item.build().clone();
        itemStack.setAmount(amount);

        player.getInventory().addItem(itemStack);
        ChatMessenger.sendStaffMessage(player, "Has recibido " + amount + "x " + item.name() + ".");
    }

    @Subcommand("summon")
    @CommandCompletion("@CataclysmMobs")
    private void summon(CommandSender sender, String mobName, @Optional Integer amount) {
        if (sender instanceof Player commandSender) {
            Location location = commandSender.getLocation();
            if (amount == null) amount = 1;
            for (int i = 0; i < amount; i++) {
                CataclysmMob mob = CataclysmMob.instantiateMob(mobName, ((CraftWorld) location.getWorld()).getHandle());
                if (mob != null) mob.addFreshEntity(location, CreatureSpawnEvent.SpawnReason.COMMAND);
            }
            Bukkit.getConsoleSender().sendMessage(commandSender.getName() + " summoned " + mobName + " (" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
        }
    }

    @Subcommand("setBlock")
    @CommandCompletion("ArcaneTable|CalamityVault")
    private void summonArcaneTable(CommandSender sender, String blockName) {
        if (!(sender instanceof Player player)) return;
        var level = ((CraftWorld) player.getWorld()).getHandle();

        switch (blockName) {
            case "ArcaneTable" -> new ArcaneTableMob(level).addFreshEntity(player.getLocation(), CreatureSpawnEvent.SpawnReason.COMMAND);
            case "CalamityVault" -> new CalamityVault(level).addFreshEntity(player.getLocation(), CreatureSpawnEvent.SpawnReason.COMMAND);
            default -> throw new IllegalArgumentException("Unknown block name: " + blockName);
        }
    }

    //TODO IMPROVE THIS
    @Subcommand("player cooldown")
    @CommandCompletion("@players")
    private void playerCooldown(String nickname) {
        Bukkit.getConsoleSender().sendMessage(nickname);
        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        if (cataclysmPlayer == null) throw new NullPointerException("Cataclysm Player is NULL.");
        cataclysmPlayer.getCooldownManager().getCooldowns().forEach(playerCooldown -> playerCooldown.stop(cataclysmPlayer.getData()));
    }

    @Subcommand("player mortality set")
    @CommandCompletion("@players <float>")
    private void playerMortality(CommandSender commandSender, String nickname, float decimal) {
        if (!(commandSender instanceof Player sender)) return;

        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        cataclysmPlayer.getMortalityManager().setValue(decimal);
        ChatMessenger.sendStaffMessage(sender, nickname + "'s mortality: " + cataclysmPlayer.getMortalityManager().getPercentage());
    }

    @Subcommand("player mortality get")
    @CommandCompletion("@players")
    private void playerMortalityGet(CommandSender commandSender, String nickname) {
        if (!(commandSender instanceof Player sender)) return;

        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        ChatMessenger.sendStaffMessage(sender, nickname + "'s mortality: " + cataclysmPlayer.getMortalityManager().getPercentage());
    }

    @Subcommand("player totems set")
    @CommandCompletion("@players <value>")
    private void playerTotemsSet(CommandSender commandSender,  String nickname, int value) {
        if (!(commandSender instanceof Player sender)) return;
        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        cataclysmPlayer.getTotemManager().setPoppedTotems(value);
        ChatMessenger.sendStaffMessage(sender, nickname + "'s popped totems: " + cataclysmPlayer.getTotemManager().getPoppedTotems());
    }

    @Subcommand("player totems get")
    @CommandCompletion("@players")
    private void playerTotemsGet(CommandSender commandSender, String nickname) {
        if (!(commandSender instanceof Player sender)) return;

        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        ChatMessenger.sendStaffMessage(sender, nickname + "'s popped totems: " + cataclysmPlayer.getTotemManager().getPoppedTotems());
    }

    @Subcommand("player deathmessage set")
    @CommandCompletion("@players <message>")
    private void playerDeathMessageSet(String nickname, String deathMessage) {
        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        cataclysmPlayer.getDeathMessageManager().setDeathMessage(deathMessage);
    }

    @Subcommand("player deathmessage get")
    @CommandCompletion("@players")
    private void playerDeathMessageGet(CommandSender commandSender, String nickname) {
        if (!(commandSender instanceof Player sender)) return;

        CataclysmPlayer cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(nickname);
        ChatMessenger.sendStaffMessage(sender, cataclysmPlayer.getDeathMessageManager().getFormattedChatMessage());
    }

    @Subcommand("player upgrades clear")
    @CommandCompletion("@players")
    private void upgradesClear(CommandSender commandSender, String nickname) {
        if (!(commandSender instanceof Player sender)) return;
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        var um = CataclysmPlayer.getCataclysmPlayer(player).getUpgradeManager();
        for (var upgrade : Upgrades.values()) {
            um.setUpgradeLevel(upgrade, 0);
        }

        ChatMessenger.sendStaffMessage(sender, "Cleared " + nickname + "'s upgrades");
    }

    @Subcommand("player upgrades set")
    @CommandCompletion("@players <value>")
    private void upgradesSet(CommandSender commandSender, String nickname, Upgrades upgrade, int level) {
        if (!(commandSender instanceof Player sender)) return;
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        var cataclysmPlayer = CataclysmPlayer.getCataclysmPlayer(player);
        cataclysmPlayer.getUpgradeManager().setUpgradeLevel(upgrade, level);

        ChatMessenger.sendStaffMessage(sender, "Set upgrade " + upgrade.name() + "for player " + nickname);
    }

    @Subcommand("player incursionreward set")
    @CommandCompletion("@players <value>")
    private void playerIncursionRewardSet(String nickname, int value) {
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        PersistentData.set(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER, value);
    }

    @Subcommand("player incursionreward get")
    @CommandCompletion("@players")
    private void playerIncursionRewardGet(CommandSender commandSender, String nickname) {
        if (!(commandSender instanceof Player sender)) return;

        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        var completedIncursions = PersistentData.get(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER);
        ChatMessenger.sendStaffMessage(sender, "Completed incursions: " + completedIncursions);
    }

    @Subcommand("player incursionhealth set")
    @CommandCompletion("@players <incursionsPassed>")
    private void playerIncursionHealthSet(String nickname, int completedIncursions) {
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        PersistentData.set(player, "INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER, completedIncursions);
    }

    @Subcommand("player incursionhealth get")
    @CommandCompletion("@players <incursionsCompleted>")
    private void playerIncursionHealthGet(String nickname) {
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        var completedIncursions = PersistentData.get(player, "INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER);
        ChatMessenger.sendStaffMessage(player, "Incursions with extra health reward completed: " + completedIncursions);
    }

    @Subcommand("player noincursionhealth set")
    @CommandCompletion("@players <uncompletedIncursions>")
    private void playerNoIncursionHealthSet(String nickname, int notCompletedIncursions) {
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        PersistentData.set(player, "NO_INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER, notCompletedIncursions);
    }

    @Subcommand("player noincursionhealth get")
    @CommandCompletion("@players <notCompletedIncursions>")
    private void playerNoIncursionHealthGet(String nickname) {
        var player = Bukkit.getPlayer(nickname);
        if (player == null) return;
        var notCompletedIncursions = PersistentData.get(player, "NO_INCURSION_EXTRA_HEALTH", PersistentDataType.INTEGER);
        ChatMessenger.sendStaffMessage(player, "Incursions with extra health reward not completed: " + notCompletedIncursions);
    }

    @Subcommand("broadcast")
    private void broadcast(String[] message) {
        if (message.length == 0) return;
        StringBuilder builder = new StringBuilder();
        for (String part : message) {
            builder.append(part).append(" ");
        }
        String finalMessage = builder.toString();
        ChatMessenger.broadcastMessage(ChatMessenger.getTextColor() + finalMessage);
        for (Player player : Bukkit.getOnlinePlayers()) player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0F, 1.0F);
    }

    @Subcommand("ragnarok start")
    @CommandCompletion("<value>")
    private void ragnarokStart(double value) {
        int hours = (int) (value * 3600);
        var ragnarok = Cataclysm.getRagnarok();
        int deathCount = Cataclysm.getGameManager().data().getDeathCount();
        int level = ((int) Math.floor(((double) deathCount / 10))) + 1;

        if (ragnarok != null) {
            var data = ragnarok.getData();
            data.setDuration(hours);
            data.setTimeLeft(hours);
            data.setLevel(level);
            ragnarok.setData(ragnarok.getData().append(data));

        } else {
            var data = new RagnarokData(hours, level);
            new RagnarokManager(new Ragnarok(data)).start();
        }
    }

    @Subcommand("ragnarok stop")
    private void ragnarokStop(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        Ragnarok ragnarok = Cataclysm.getRagnarok();

        if (ragnarok == null) {
            ChatMessenger.sendStaffMessage(player, "No hay ragnarok activa");
            return;
        }

        new RagnarokManager(ragnarok).stop();
    }



    @Subcommand("deathcount")
    private void deathCount(CommandSender commandSender, @Optional Integer deathCount) {
        if (!(commandSender instanceof Player player)) return;

        var data = Cataclysm.getGameManager().data();
        if (deathCount != null) data.setDeathCount(deathCount);
        ChatMessenger.sendStaffMessage(player, "Deathcount: " + data.getDeathCount());
    }

    @Subcommand("day set")
    private void setDay(CommandSender commandSender, int day) {
        if (!(commandSender instanceof Player)) return;
        Cataclysm.getDayManager().setDay(day);
    }

    @Subcommand("damagetwisted")
    private void damage(CommandSender commandSender) {
        if (!(commandSender instanceof Player player)) return;
        TwistedWarden.damagePlayersSeeing(player);
    }

}