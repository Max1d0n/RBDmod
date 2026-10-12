package com.example.rdbmod;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

public class CheckpointManager {

    private static final Logger LOGGER = LogUtils.getLogger();

    // List of all dimension and world data folders
    private static final String[] WORLD_FOLDERS = {
            "region",      // Overworld
            "entities",    // Overworld entities
            "data",        // Maps, structures, datapacks
            "poi",         // Points of interest (villagers, portals)
            "DIM-1",       // Nether
            "DIM1",        // End
            "dimensions"   // Modded dimensions
    };

    public static void createCheckpoint(MinecraftServer server) {
        LOGGER.info("[RBD] === Creating checkpoint ===");
        try {
            server.saveEverything(true, true, true);

            Path worldDir = server.getWorldPath(LevelResource.ROOT).normalize();
            Path checkpointDir = worldDir.resolve("rbd_checkpoint");

            deleteDirectory(checkpointDir);
            Files.createDirectories(checkpointDir);

            // Copy all dimension folders
            for (String folderName : WORLD_FOLDERS) {
                Path sourceFolder = worldDir.resolve(folderName);
                if (Files.exists(sourceFolder)) {
                    copyFolder(sourceFolder, checkpointDir.resolve(folderName));
                }
            }

            // Copy main world state file
            Files.copy(worldDir.resolve("level.dat"), checkpointDir.resolve("level.dat"), StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("[RBD] === Checkpoint successfully created! ===");
        } catch (Exception e) {
            LOGGER.error("[RBD] Error creating checkpoint!", e);
        }
    }

    public static boolean restoreCheckpoint(Path worldDir) {
        Path checkpointDir = worldDir.resolve("rbd_checkpoint");

        if (!Files.exists(checkpointDir)) {
            LOGGER.error("[RBD] Error: Checkpoint directory not found: {}", checkpointDir);
            return false;
        }

        try {
            // Delete current dimension folders and restore them from checkpoint
            for (String folderName : WORLD_FOLDERS) {
                deleteDirectory(worldDir.resolve(folderName));

                Path checkpointFolder = checkpointDir.resolve(folderName);
                if (Files.exists(checkpointFolder)) {
                    copyFolder(checkpointFolder, worldDir.resolve(folderName));
                }
            }

            Files.copy(checkpointDir.resolve("level.dat"), worldDir.resolve("level.dat"), StandardCopyOption.REPLACE_EXISTING);

            LOGGER.info("[RBD] === World successfully restored from checkpoint! ===");
            return true;
        } catch (Exception e) {
            LOGGER.warn("[RBD] File access error (Windows file lock): {}", e.getMessage());
            return false;
        }
    }

    private static void copyFolder(Path src, Path dest) throws IOException {
        if (!Files.exists(src)) return;
        try (Stream<Path> stream = Files.walk(src)) {
            stream.forEach(source -> {
                try {
                    Path destination = dest.resolve(src.relativize(source));
                    if (Files.isDirectory(source)) {
                        if (!Files.exists(destination)) Files.createDirectories(destination);
                    } else {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private static void deleteDirectory(Path path) throws IOException {
        if (!Files.exists(path)) return;
        try (Stream<Path> stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
    }
}