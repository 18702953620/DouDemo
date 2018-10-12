package com.ch.doudemo.widget;

import android.text.TextUtils;

import com.danikula.videocache.ProxyCacheUtils;
import com.danikula.videocache.file.FileNameGenerator;

/**
 * 作者： ch
 * 时间： 2018/10/11 0011-上午 10:12
 * 描述： 自定义缓存文件名
 * 来源：
 */

public class MyFileNameGenerator implements FileNameGenerator {
    private static final int MAX_EXTENSION_LENGTH = 4;

    @Override
    public String generate(String url) {
        String extension = getExtension(url);
        int dotIndex = url.lastIndexOf('.');

        if (url.length() > 18 && dotIndex > 18) {
            return url.substring(dotIndex - 18);
        }
        String name = ProxyCacheUtils.computeMD5(url);
        return TextUtils.isEmpty(extension) ? name : name + "." + extension;
    }

    private String getExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
                url.substring(dotIndex + 1, url.length()) : "";
    }
}
