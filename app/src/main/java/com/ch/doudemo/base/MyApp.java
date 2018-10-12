package com.ch.doudemo.base;

import android.app.Application;
import android.content.Context;

import com.ch.doudemo.widget.MyFileNameGenerator;
import com.danikula.videocache.HttpProxyCacheServer;

/**
 * 作者： ch
 * 时间： 2018/10/12 0012-上午 11:34
 * 描述：
 * 来源：
 */

public class MyApp extends Application {


    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApp app = (MyApp) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(1024 * 1024 * 1024)       // 1 Gb for cache
                .fileNameGenerator(new MyFileNameGenerator())
                .build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
