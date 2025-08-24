package org.cataclysm.api.structure.schematic;

import com.fastasyncworldedit.core.FaweAPI;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Getter
public class SchematicLoader {
    private final File file;

    public SchematicLoader(String schemPath) {
        this.file = new File(Cataclysm.getInstance().getDataFolder(), schemPath);
    }

    public void pasteSchematic(@NotNull Location location) {
        World weWorld = FaweAPI.getWorld(location.getWorld().getName());
        BlockVector3 pasteLocation = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation operation = new ClipboardHolder(this.loadClipboard(this.file))
                    .createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(true)
                    .copyEntities(true)
                    .build();

            Operations.complete(operation);
        }
    }

    public void pasteSchematic(@NotNull Location location, boolean ignoreAirBlocks) {
        World weWorld = FaweAPI.getWorld(location.getWorld().getName());
        BlockVector3 pasteLocation = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        try (EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            Operation operation = new ClipboardHolder(this.loadClipboard(this.file))
                    .createPaste(editSession)
                    .to(pasteLocation)
                    .ignoreAirBlocks(ignoreAirBlocks)
                    .copyEntities(true)
                    .build();
            Operations.complete(operation);
        }
    }

    private Clipboard loadClipboard(File file) {
        Clipboard clipboard;

        try (FileInputStream fis = new FileInputStream(file)) {
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            if (format == null) throw new NullPointerException("Format is null: " + file.getAbsolutePath());
            ClipboardReader reader = format.getReader(fis);
            clipboard = reader.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return clipboard;
    }
}