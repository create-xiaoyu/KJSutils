package com.xiaoyu.kjsutils.event;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "kjsutils")
public class KJSutilsPlayerEventHandler {

    @SubscribeEvent
    public static void onChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ResourceKey<Level> loggedOut = event.getFrom();
        ResourceKey<Level> loggedIn = player.level().dimension();

        if (KJSutilEvents.PLAYER_LOGGED_OUT_DIMENSION.hasListeners(loggedOut)) {
            KJSutilEvents.PLAYER_LOGGED_OUT_DIMENSION.post(
                    new PlayerDimensionKubeEvent(player, loggedOut, loggedIn),
                    loggedOut
            );
        }

        if (KJSutilEvents.PLAYER_LOGGED_IN_DIMENSION.hasListeners(loggedIn)) {
            KJSutilEvents.PLAYER_LOGGED_IN_DIMENSION.post(
                    new PlayerDimensionKubeEvent(player, loggedOut, loggedIn),
                    loggedIn
            );
        }
    }
}