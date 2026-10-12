package com.example.rdbmod;

import com.example.rdbmod.client.RBDConfigScreen;
import com.example.rdbmod.network.TriggerRollbackPayload;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ReturnByDeath.MODID)
public class ReturnByDeath {
    public static final String MODID = "returnbydeath";

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> REWIND_SOUND =
            SOUND_EVENTS.register("rewind", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "rewind")));

    public static final DeferredHolder<SoundEvent, SoundEvent> WITCH_CALL_SOUND =
            SOUND_EVENTS.register("witch_call", () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(MODID, "witch_call")));

    public ReturnByDeath(IEventBus modEventBus, ModContainer modContainer) {
        SOUND_EVENTS.register(modEventBus);

        modEventBus.addListener(this::registerPayloads);

        modContainer.registerConfig(ModConfig.Type.COMMON, RBDConfig.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modContainer.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (container, screen) -> new RBDConfigScreen(screen)
            );
        }
    }

    private void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(
                TriggerRollbackPayload.TYPE,
                TriggerRollbackPayload.STREAM_CODEC,
                com.example.rdbmod.client.ClientEvents::handleRollbackPacket
        );
    }
}