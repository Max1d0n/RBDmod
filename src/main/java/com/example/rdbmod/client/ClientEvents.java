package com.example.rdbmod.client;

import com.example.rdbmod.CheckpointManager;
import com.example.rdbmod.RBDConfig;
import com.example.rdbmod.ReturnByDeath;
import com.example.rdbmod.network.TriggerRollbackPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.slf4j.Logger;

import java.nio.file.Path;

@EventBusSubscriber(modid = ReturnByDeath.MODID, value = Dist.CLIENT)
public class ClientEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    private enum RollbackState {
        IDLE,
        WAITING_FOR_SERVER_STOP,
        RESTORING_FILES
    }

    private static RollbackState currentState = RollbackState.IDLE;
    private static String targetWorldFolder = null;
    private static MinecraftServer serverInstance = null;

    private static int rewindTimer = -1;
    private static int witchCallTimer = -1;

    public static void handleRollbackPacket(TriggerRollbackPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getSingleplayerServer() != null) {
                serverInstance = mc.getSingleplayerServer();

                Path normalizedPath = serverInstance
                        .getWorldPath(LevelResource.ROOT)
                        .normalize();

                targetWorldFolder = normalizedPath.getFileName().toString();
                LOGGER.info("[RBD] Triggering rollback for world: {}", targetWorldFolder);

                mc.setScreen(new RBDTransitionScreen());
                currentState = RollbackState.WAITING_FOR_SERVER_STOP;
            }
        });
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();

        // 1. Wait for server thread to stop completely
        if (currentState == RollbackState.WAITING_FOR_SERVER_STOP) {
            if (!(mc.screen instanceof RBDTransitionScreen)) {
                mc.setScreen(new RBDTransitionScreen());
            }

            if (serverInstance != null && serverInstance.isStopped()) {
                LOGGER.info("[RBD] Server thread completely stopped! Disconnecting client...");

                mc.disconnect(new RBDTransitionScreen());
                serverInstance = null;

                currentState = RollbackState.RESTORING_FILES;
            }
        }
        // 2. Restore files from checkpoint
        else if (currentState == RollbackState.RESTORING_FILES) {
            if (targetWorldFolder != null) {
                Path savesDir = mc.gameDirectory.toPath().resolve("saves").resolve(targetWorldFolder);

                boolean restored = CheckpointManager.restoreCheckpoint(savesDir);

                if (restored) {
                    LOGGER.info("[RBD] Files restored! Reopening world...");
                    currentState = RollbackState.IDLE;

                    rewindTimer = RBDConfig.REWIND_DELAY_TICKS.get();
                    witchCallTimer = RBDConfig.WITCH_CALL_DELAY_TICKS.get();

                    String folderToOpen = targetWorldFolder;
                    targetWorldFolder = null;

                    mc.createWorldOpenFlows().openWorld(folderToOpen, () -> {});
                } else {
                    LOGGER.warn("[RBD] Windows file access delay, retrying...");
                }
            }
        }

        // 3. Play sounds post-spawn
        if (mc.level != null && mc.player != null) {
            if (rewindTimer > 0) {
                rewindTimer--;
                if (rewindTimer == 0) {
                    LOGGER.info("[RBD] Playing REWIND sound");
                    mc.getSoundManager().play(
                            SimpleSoundInstance.forUI(ReturnByDeath.REWIND_SOUND.get(), 1.0F, 1.0F)
                    );
                    rewindTimer = -1;
                }
            }

            if (witchCallTimer > 0) {
                witchCallTimer--;
                if (witchCallTimer == 0) {
                    LOGGER.info("[RBD] Playing WITCH CALL sound");
                    mc.getSoundManager().play(
                            SimpleSoundInstance.forUI(ReturnByDeath.WITCH_CALL_SOUND.get(), 1.0F, 1.0F)
                    );
                    witchCallTimer = -1;
                }
            }
        }
    }
}