package com.xiaoyu.kjsutils;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class KJSutilsPlugin implements KubeJSPlugin {
    public static final EventGroup GROUP = EventGroup.of("KJSutils");
    public static final EventHandler HTTP_DOWNLOAD = GROUP.common("HttpDownload", () -> KJSutilsEvent.class);

    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("KJSutils", new KJSutilsWrapper());
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(GROUP);
    }
}
