package com.example.rdbmod.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TriggerRollbackPayload() implements CustomPacketPayload {
    public static final Type<TriggerRollbackPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("returnbydeath", "trigger_rollback"));

    public static final StreamCodec<ByteBuf, TriggerRollbackPayload> STREAM_CODEC =
            StreamCodec.unit(new TriggerRollbackPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}