package com.xiaoyu.kjsutils.event;

import dev.latvian.mods.kubejs.event.KubeEvent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class PlayerDimensionKubeEvent implements KubeEvent {
    private final ServerPlayer player;
    private final ResourceKey<Level> loggedOut;
    private final ResourceKey<Level> loggedIn;

    public PlayerDimensionKubeEvent(ServerPlayer player, ResourceKey<Level> loggedOut, ResourceKey<Level> loggedIn) {
        this.player = player;
        this.loggedOut = loggedOut;
        this.loggedIn = loggedIn;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public String getLoggedOut() {
        return loggedOut.location().toString();
    }

    public String getLoggedIn() {
        return loggedIn.location().toString();
    }
}
