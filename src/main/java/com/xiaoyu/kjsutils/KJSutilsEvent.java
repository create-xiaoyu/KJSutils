package com.xiaoyu.kjsutils;

import dev.latvian.mods.kubejs.event.KubeEvent;

public class KJSutilsEvent implements KubeEvent {
    private final String url;
    private final String path;
    private final String code;

    public KJSutilsEvent(String url, String path, String code) {
        this.url = url;
        this.path = path;
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public String getCode() {
        return code;
    }
}
