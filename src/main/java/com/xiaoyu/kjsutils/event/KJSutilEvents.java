package com.xiaoyu.kjsutils.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventTargetType;
import dev.latvian.mods.kubejs.event.TargetedEventHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public interface KJSutilEvents {

    EventGroup GROUP = EventGroup.of("KJSutilEvents");

    EventTargetType<ResourceKey<Level>> DIMENSION_TARGET =
            EventTargetType.registryKey(Registries.DIMENSION, Level.class);

    TargetedEventHandler<ResourceKey<Level>> PLAYER_LOGGED_IN_DIMENSION =
            GROUP.server("playerLoggedInDimension",
                            () -> PlayerDimensionKubeEvent.class)
                    .supportsTarget(DIMENSION_TARGET);

    TargetedEventHandler<ResourceKey<Level>> PLAYER_LOGGED_OUT_DIMENSION =
            GROUP.server("playerLoggedOutDimension",
                            () -> PlayerDimensionKubeEvent.class)
                    .supportsTarget(DIMENSION_TARGET);
}