package com.example.rdbmod.client;

import com.example.rdbmod.RBDConfig;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class DynamicRBDSoundInstance extends AbstractTickableSoundInstance {

    public DynamicRBDSoundInstance(SoundEvent soundEvent) {
        super(soundEvent, SoundSource.MASTER, RandomSource.create());
        this.attenuation = Attenuation.NONE; // UI-звук (без затухания по расстоянию)
        this.relative = true;               // Привязан к слуху игрока
        this.volume = RBDConfig.SOUND_VOLUME.get().floatValue();
    }

    @Override
    public void tick() {
        this.volume = RBDConfig.SOUND_VOLUME.get().floatValue();
    }
}