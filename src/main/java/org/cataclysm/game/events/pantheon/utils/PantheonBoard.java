package org.cataclysm.game.events.pantheon.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.scoreboard.FastBoard;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonBoard {
    private ScheduledFuture<?> loop;
    private final PantheonOfCataclysm pantheon;
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public PantheonBoard(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void startTick() {
        this.loop = this.pantheon.getExecutor().scheduleAtFixedRate(() ->
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::update), 0, 800, TimeUnit.MILLISECONDS);
    }

    public void stopTick() {
        if (this.loop == null) return;
        this.loop.cancel(true);
        this.loop = null;
        clear();
    }

    public void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isOp()) continue;
            this.create(player);
        }
    }

    public void create(Player player) {
        FastBoard board = this.boards.computeIfAbsent(player.getUniqueId(), id -> new FastBoard(player));
        board.updateTitle("ᴘᴀɴᴛʜᴇᴏɴ ᴏꜰ ᴄᴀᴛᴀᴄʟʏꜱᴍ");
        board.updateLines(
                controller(),
                profiles()
        );
    }

    public void remove(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());
        if (board != null) {
            board.delete();
        }
    }

    public void clear() {
        this.boards.values().forEach(FastBoard::delete);
        this.boards.clear();
    }

    private String controller() {
        String display = this.pantheon.getController() == null ? "ɴ/ᴀ" : this.pantheon.getController().getName();
        return "ᴄᴏɴᴛʀᴏʟʟᴇʀ: " + display;
    }

    private String profiles() {
        return "ᴘʀᴏꜰɪʟᴇꜱ: " + this.pantheon.getConfigurator().getRegistry().getActiveProfiles().size();
    }
}