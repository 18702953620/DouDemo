package com.ch.doudemo.widget;

import android.content.Context;
import android.media.AudioManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ch.doudemo.R;
import com.ch.doudemo.base.MyApp;
import com.danikula.videocache.HttpProxyCacheServer;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 作者： ch
 * 时间： 2018/8/17 0017-下午 5:14
 * 描述：
 * 来源：
 */


public class MyVideoPlayer extends JZVideoPlayerStandard {
    private RelativeLayout rl_touch_help;
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

        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            onStateAutoComplete();
            setUp((String) getCurrentUrl(), JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN);
        } else {
            super.onAutoCompletion();
            setUp((String) getCurrentUrl(), JZVideoPlayer.CURRENT_STATE_NORMAL);
        }
        //循环播放

        startVideo();
    }

    @Override
    public void setUp(String url, int screen, Object... objects) {

        if (url.startsWith("http")) {
            HttpProxyCacheServer proxy = MyApp.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(url);
            super.setUp(proxyUrl, screen, objects);
        } else {
            super.setUp(url, screen, objects);
        }
    }

    @Override
    public void init(final Context context) {
        super.init(context);

        rl_touch_help = findViewById(R.id.rl_touch_help);
        ll_start = findViewById(R.id.ll_start);
        iv_start = findViewById(R.id.iv_start);
        resetPlayView();

        rl_touch_help.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPlayView();
                if (isPlay()) {
                    fullscreenButton.performClick();
                }

            }
        });

        ll_start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay()) {
                    goOnPlayOnPause();
                } else {
                    //暂停
                    if (currentState == JZVideoPlayer.CURRENT_STATE_PAUSE) {
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
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN) {
            initTextureView();
            addTextureView();
            AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            JZUtils.scanForActivity(getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            JZMediaManager.setDataSource(dataSourceObjects);
            JZMediaManager.setCurrentDataSource(JZUtils.getCurrentFromDataSource(dataSourceObjects, currentUrlMapIndex));
            JZMediaManager.instance().positionInList = positionInList;
            onStatePreparing();
            ll_start.setVisibility(VISIBLE);
        } else {
            super.startVideo();
            ll_start.setVisibility(GONE);
        }
        resetPlayView();
    }

    @Override
    public void startWindowTiny() {
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
        if (currentState == CURRENT_STATE_PREPARING || currentState == CURRENT_STATE_PLAYING || currentState == -1) {
            return true;
        }

        return false;
    }

}
