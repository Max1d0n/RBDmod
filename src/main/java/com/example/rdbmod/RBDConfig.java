package com.example.rdbmod;

import net.neoforged.neoforge.common.ModConfigSpec;

public class RBDConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = BUILDER
            .comment("Enables or disables Return by Death mechanic")
            .define("enabled", true);

    public static final ModConfigSpec.DoubleValue SOUND_VOLUME = BUILDER
            .comment("Master volume for all mod sound effects (0.0 = silent, 1.0 = 100%)")
            .defineInRange("soundVolume", 1.0, 0.0, 1.0);

    public static final ModConfigSpec.IntValue REWIND_DELAY_TICKS = BUILDER
            .comment("Delay for playing rewind sound after spawn (in ticks, 20 ticks = 1 sec)")
            .defineInRange("rewindDelayTicks", 2, 0, 200);

    public static final ModConfigSpec.IntValue WITCH_CALL_DELAY_TICKS = BUILDER
            .comment("Delay for playing Witch Call sound (in ticks, 160 ticks = 8 sec)")
            .defineInRange("witchCallDelayTicks", 160, 0, 1200);

    public static final ModConfigSpec SPEC = BUILDER.build();
}