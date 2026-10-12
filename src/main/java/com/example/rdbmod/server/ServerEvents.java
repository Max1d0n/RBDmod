package com.example.rdbmod.server;

import com.example.rdbmod.CheckpointManager;
import com.example.rdbmod.RBDConfig;
import com.example.rdbmod.network.TriggerRollbackPayload;
import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

@EventBusSubscriber(modid = "returnbydeath")
public class ServerEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        if (!RBDConfig.ENABLED.get()) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            LOGGER.info("[RBD] Player woke up. Creating checkpoint...");
            CheckpointManager.createCheckpoint(player.getServer());
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!RBDConfig.ENABLED.get()) {
            return;
        }

        if (event.getEntity() instanceof ServerPlayer player) {
            Path worldDir = player.getServer().getWorldPath(LevelResource.ROOT).normalize();
            Path checkpointDir = worldDir.resolve("rbd_checkpoint");

            if (Files.exists(checkpointDir)) {
                LOGGER.info("[RBD] Player died. Halting server...");
                event.setCanceled(true);

                player.setHealth(player.getMaxHealth());
                player.clearFire();
                player.removeAllEffects();
                player.setInvulnerable(true);

                PacketDistributor.sendToPlayer(player, new TriggerRollbackPayload());

                player.getServer().halt(false);
            }
        }
    }
}