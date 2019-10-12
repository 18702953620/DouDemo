package com.ch.doudemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.ch.doudemo.R;
import com.ch.doudemo.widget.MyVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 视频预览
 */
public class PrepareActivity extends AppCompatActivity {

    @BindView(R.id.mp_video)
    MyVideoPlayer mpVideo;

    public static final String VIDEO_PATH = "VIDEO_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prepare);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String path = getIntent().getStringExtra(VIDEO_PATH);
        if (!TextUtils.isEmpty(path)) {
            mpVideo.setUp(path, path, MyVideoPlayer.STATE_NORMAL);
            mpVideo.startVideo();
        }
    }
}
