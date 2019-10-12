package com.ch.doudemo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ch.doudemo.R;
import com.ch.doudemo.base.MyApp;
import com.danikula.videocache.HttpProxyCacheServer;

import cn.jzvd.JzvdStd;


/**
 * 作者： ch
 * 时间： 2018/8/17 0017-下午 5:14
 * 描述：
 * 来源：
 */


public class MyVideoPlayer extends JzvdStd {
    public RelativeLayout rl_touch_help;
    private ImageView iv_start;
    private LinearLayout ll_start;

    private Context context;


    public MyVideoPlayer(Context context) {
        super(context);
        this.context = context;
    }

    public MyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void onAutoCompletion() {

        thumbImageView.setVisibility(View.GONE);

        if (screen == SCREEN_FULLSCREEN) {
            onStateAutoComplete();
            setUp((String) jzDataSource.getCurrentUrl(), jzDataSource.title, SCREEN_FULLSCREEN);
        } else {
            super.onAutoCompletion();
            setUp((String) jzDataSource.getCurrentUrl(), jzDataSource.title, SCREEN_NORMAL);
        }
        //循环播放
        startVideo();
    }


    @Override
    public void setUp(String url, String title, int screen) {
        super.setUp(url, title, screen);
        if (url.startsWith("http")) {
            HttpProxyCacheServer proxy = MyApp.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(url);
            super.setUp(proxyUrl, title, screen);
        } else {
            super.setUp(url, title, screen);
        }

    }

    @Override
    public void init(final Context context) {
        super.init(context);

        rl_touch_help = findViewById(R.id.rl_touch_help);
        ll_start = findViewById(R.id.ll_start);
        iv_start = findViewById(R.id.iv_start);
        resetPlayView();

        rl_touch_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPlayView();
                if (isPlay()) {
                    fullscreenButton.performClick();
                }

            }
        });

        ll_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay()) {
                    goOnPlayOnPause();
                } else {
                    //暂停
                    if (state == STATE_PAUSE) {
                        goOnPlayOnResume();
                    } else {
                        startVideo();
                    }
                }
                resetPlayView();
            }
        });
    }

    @Override
    public void startVideo() {
        if (screen == SCREEN_FULLSCREEN) {
            startFullscreenDirectly(context, MyVideoPlayer.class, jzDataSource);
            onStatePreparing();
            ll_start.setVisibility(VISIBLE);
        } else {
            super.startVideo();
            ll_start.setVisibility(GONE);
        }
        resetPlayView();
    }


    private void resetPlayView() {
        if (isPlay()) {
            iv_start.setBackgroundResource(R.mipmap.video_play_parse);
        } else {
            iv_start.setBackgroundResource(R.mipmap.stop);
        }
    }

    /**
     * 是否播放
     *
     * @return
     */
    private boolean isPlay() {
        if (state == STATE_PREPARING || state == STATE_PLAYING || state == -1) {
            return true;
        }

        return false;
    }

}
