package com.xiaoyu.kjsutils;

import com.xiaoyu.kjsutils.event.KJSutilEvents;
import dev.latvian.mods.kubejs.event.EventGroupRegistry;
import dev.latvian.mods.kubejs.plugin.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingRegistry;

public class KJSutilsPlugin implements KubeJSPlugin {
    @Override
    public void registerBindings(BindingRegistry bindings) {
        bindings.add("KJSutils", new KJSutilsWrapper());
    }

    @Override
    public void registerEvents(EventGroupRegistry registry) {
        registry.register(KJSutilEvents.GROUP);
    }
}
